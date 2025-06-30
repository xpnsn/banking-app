package com.safevault.api_gateway.filter;

import com.fasterxml.jackson.core.filter.TokenFilter;
import com.safevault.api_gateway.feignclient.UserClient;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String JWT_SECRET;
    @Value("${secret.key}")
    private String SECRET_KEY;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();
            ServerHttpRequest request = exchange.getRequest();
            if(path.equals("/api/v1/security/login")||path.equals("/api/v1/security/register")) {
                request = exchange.getRequest().mutate().header("X-Secret-Key", SECRET_KEY).build();
                return chain.filter(exchange.mutate().request(request).build());
            }
            String token = getToken(exchange);
            String userId;

            try {
                Jws<Claims> claimsJws = Jwts.parser()
                        .setSigningKey(JWT_SECRET)
                        .build().parseClaimsJws(token);
                Claims claims = claimsJws.getPayload();
                userId = String.valueOf(claims.get("id", Object.class));
                boolean isVerified = claims.get("verified", Boolean.class);
                List<String> roles = ((List<?>) claims.get("roles", Object.class))
                        .stream()
                        .map(role -> (String) ((Map<?, ?>) role).get("authority"))
                        .collect(Collectors.toList());

                request = exchange.getRequest().mutate()
                        .header("X-Secret-Key", SECRET_KEY)
                        .header("X-User-Id", userId)
                        .header("X-Authorities", String.join(",", roles))
                        .header("X-Username", claims.getSubject())
                        .build();

                if(path.contains("generate-otp") || path.contains("validate-otp")) {
                    return chain.filter(exchange.mutate().request(request).build());
                }
                if(!isVerified) throw new RuntimeException("User not verified");
            } catch (Exception e) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                byte[] bytes = e.getMessage().getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bytes);
                return response.writeWith(Mono.just(buffer));
            }
            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    private static String getToken(ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        if(!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new RuntimeException("Missing Authorization header");
        }
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }
        return authHeader.substring(7);
    }

    public static class Config{}
}

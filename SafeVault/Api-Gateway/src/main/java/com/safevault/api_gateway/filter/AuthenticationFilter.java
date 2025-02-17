package com.safevault.api_gateway.filter;

import com.fasterxml.jackson.core.filter.TokenFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    public static final String JWT_SECRET = "1B4BAF6A13B5301A3411C93C33630E8F7F219C5F8F3344E7CF";

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(AuthenticationFilter.Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();
            ServerHttpRequest request = exchange.getRequest();
            if(path.equals("/api/v1/security/login")||path.equals("/api/v1/security/register")) {
                request = exchange.getRequest().mutate().header("X-Secret-Key", "SECRET").build();
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

                request = exchange.getRequest().mutate()
                        .header("X-Secret-Key", "SECRET")
//                        .header("X-User-Id", )
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Invalid token", e);
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

    public static class Config{

    }
}

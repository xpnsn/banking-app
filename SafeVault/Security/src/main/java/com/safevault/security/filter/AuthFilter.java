package com.safevault.security.filter;

import com.safevault.security.entity.UserEntity;
import com.safevault.security.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final AuthService authService;

    public AuthFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.endsWith("/login") || path.endsWith("/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String userIdHeader = request.getHeader("X-User-Id");
        String authoritiesHeader = request.getHeader("X-Authorities");

        if (userIdHeader == null || authoritiesHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UserEntity user;
        try {
            user = authService.getUserByUserId(userIdHeader);
        } catch (NumberFormatException e) {
            logger.error("Invalid User ID format: " + userIdHeader, e);
            filterChain.doFilter(request, response);
            return;
        }

        if (user != null) {
            List<GrantedAuthority> authorityList = Arrays.stream(authoritiesHeader.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword(),
                    authorityList
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}

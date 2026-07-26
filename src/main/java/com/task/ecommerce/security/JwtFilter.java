package com.task.ecommerce.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.ecommerce.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    private final ObjectMapper objectMapper =
            new ObjectMapper().findAndRegisterModules();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Token ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(6);

        try {

            UUID jti = jwtUtil.extractJti(token);

            if (tokenBlacklistService.isBlacklisted(jti)) {
                sendError(response, "Token has been logged out.");
                return;
            }

            String username = jwtUtil.extractUsername(token);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            sendError(response, "Token expired, please login again");
            return;

        } catch (MalformedJwtException |
                 SignatureException |
                 IllegalArgumentException e) {

            sendError(response, "Invalid token");
            return;
        }
    }

    private void sendError(HttpServletResponse response, String message)
            throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", message);

        response.getWriter()
                .write(objectMapper.writeValueAsString(body));
    }
}
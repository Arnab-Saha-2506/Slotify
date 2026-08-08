package com.proj.slotify.security;

import com.proj.slotify.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException{
        String authHeader = request.getHeader("Authorization");
        String requestPath = request.getRequestURI();

        logger.debug("[JwtAuthFilter] Processing request: method={}, path={}", request.getMethod(), requestPath);
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            logger.debug("[JwtAuthFilter] No Bearer token found for path={}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        logger.debug("[JwtAuthFilter] Bearer token extracted for path={}", requestPath);

        // 2. Validate token and extract userId
        String userId;
        try {
            userId = jwtUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            logger.warn("[JwtAuthFilter] Invalid JWT token for path={}: {}", requestPath, e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }
        logger.debug("[JwtAuthFilter] JWT token valid, userId={}", userId);

        // 3. Load user from DB
        var user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.warn("[JwtAuthFilter] User not found for userId={} in JWT, path={}", userId, requestPath);
            filterChain.doFilter(request, response);
            return;
        }
        logger.debug("[JwtAuthFilter] User loaded: id={}, email={}", user.getId(), user.getEmail());

        // 4. Set authentication in SecurityContext
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        Collections.emptyList()
                );
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.debug("[JwtAuthFilter] Authentication set in SecurityContext for user id={}", user.getId());

        filterChain.doFilter(request, response);

    }

}

package com.proj.slotify.security;

import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.repository.UserRepository;
import com.proj.slotify.util.IdGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${FRONTEND_URL:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        logger.info("[OAuth2SuccessHandler] Google login success: email={}, name={}", email, name);

        if (email == null) {
            response.sendRedirect(frontendUrl + "/login?error=no_email");
            return;
        }

        UserEntity user = userRepository.findByEmail(email);
        boolean newUser = false;

        if (user == null) {
            logger.info("[OAuth2SuccessHandler] New Google user: email={}, name={}", email, name);
            user = UserEntity.builder()
                    .id(IdGenerator.generateUserId(name))
                    .name(name != null ? name : extractNameFromEmail(email))
                    .email(email)
                    .password("GOOGLE_OAUTH")
                    .timezone("UTC")
                    .authProvider(com.proj.slotify.enums.AuthProvider.GOOGLE)
                    .build();
            userRepository.save(user);
            newUser = true;
        } else {
            logger.info("[OAuth2SuccessHandler] Existing user: email={}, userId={}", email, user.getId());
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        String redirectUrl = String.format("%s/auth/callback?token=%s&newUser=%b&email=%s",
                frontendUrl, token, newUser, email);
        response.sendRedirect(redirectUrl);
    }

    private String extractNameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "User";
        }
        String localPart = email.substring(0, email.indexOf('@'));
        return localPart.substring(0, 1).toUpperCase() + localPart.substring(1);
    }
}
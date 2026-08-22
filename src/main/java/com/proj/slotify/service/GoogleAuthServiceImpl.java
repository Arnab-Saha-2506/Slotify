package com.proj.slotify.service;

import com.proj.slotify.dto.GoogleAuthResponseDTO;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.enums.AuthProvider;
import com.proj.slotify.exception.BadRequestException;
import com.proj.slotify.repository.UserRepository;
import com.proj.slotify.security.JwtUtil;
import com.proj.slotify.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class GoogleAuthServiceImpl implements GoogleAuthService{
    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthServiceImpl.class);
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RestClient restClient = RestClient.create();

    @Value("${GOOGLE_CLIENT_ID:}")
    private String googleClientId;

    private static final String GOOGLE_TOKENINFO_URL = "https://oauth2.googleapis.com/tokeninfo";

    public GoogleAuthResponseDTO authenticateWithGoogle(String idToken){
        logger.info("[GoogleAuthService] Verifying Google ID token");

        if(googleClientId == null || googleClientId.isBlank()){
            logger.warn("[GoogleAuthService] GOOGLE_CLIENT_ID not configured");
            throw new BadRequestException("Google login is not configured.");
        }

        Map<String, Object> payload = verifyGoogleToken(idToken);
        String email = (String) payload.get("email");
        String name = (String) payload.get("name");

        if(email == null){
            logger.warn("[GoogleAuthService] Email not found in Google token");
            throw new BadRequestException("Invalid Google Token: email not found");
        }

        UserEntity user = userRepository.findByEmail(email);
        boolean newUser = false;

        if(user == null){
            logger.info("[GoogleAuthService] New Google user: email={}, name={}", email, name);
            user = createGoogleUser(email, name);
            newUser = true;
        }
        else{
            logger.info("[GoogleAuthService] Existing Google user: email={}, userId={}", email, user.getId());
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        logger.info("[GoogleAuthService] JWT generated for Google user: email={}, userId={}", email, user.getId());

        return GoogleAuthResponseDTO.builder()
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .newUser(newUser)
                .build();
    }

    private Map<String, Object> verifyGoogleToken(String idToken){
        try{
            String url = GOOGLE_TOKENINFO_URL + "?id_token=" + idToken;

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(Map.class);

            if(response == null){
                throw new BadRequestException("Invalid Google token: Empty response");
            }

            String aud = (String) response.get("aud");
            if(!googleClientId.equals(aud)){
                logger.warn("[GoogleAuthService] Token audience mismatch: expected={}, got={}", googleClientId, aud);
                throw new BadRequestException("Invalid Google token: Audience mismatch");
            }

            Long exp = response.get("exp") instanceof Number? ((Number) response.get("exp")).longValue() : null;
            if(exp != null && Instant.now().getEpochSecond() > exp){
                throw new BadRequestException("Invalid Google token: Expired");
            }

            logger.info("[GoogleAuthService] Google token verified successfully for email={}", response.get("email"));
            return response;
        }
        catch (BadRequestException e){
            throw e;
        }
        catch (Exception e){
            logger.error("[GoogleAuthService] Failed to verify Google token", e);
            throw new BadRequestException("Invalid google token");
        }
    }

    private UserEntity createGoogleUser(String email, String name){
        UserEntity user = UserEntity.builder()
                .id(IdGenerator.generateUserId(name))
                .name(name!=null?name:extractNameFromEmail(email))
                .email(email)
                .password("GOOGLE_OAUTH")
                .timezone("UTC")
                .authProvider(AuthProvider.GOOGLE)
                .build();

        UserEntity saved = userRepository.save(user);
        logger.info("[GoogleAuthService] Created new user: id={}, email={}", saved.getId(), saved.getEmail());
        return saved;
    }

    private String extractNameFromEmail(String email){
        if(email == null || !email.contains("@")){
            return "User";
        }
        String localPart = email.substring(0, email.indexOf('@'));
        return localPart.substring(0,1).toUpperCase()+localPart.substring(1);
    }
}

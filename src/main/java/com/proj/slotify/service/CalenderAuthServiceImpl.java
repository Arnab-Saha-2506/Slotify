package com.proj.slotify.service;

import com.proj.slotify.entity.GoogleCalenderCredentialEntity;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.repository.GoogleCalenderCredentialRepository;
import com.proj.slotify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CalenderAuthServiceImpl implements CalenderAuthService{
    private static final Logger logger = LoggerFactory.getLogger(CalenderAuthServiceImpl.class);
    private final UserRepository userRepository;
    private final GoogleCalenderCredentialRepository googleCalenderCredentialRepository;
    private final RestClient restClient = RestClient.create();

    @Value("${GOOGLE_CLIENT_ID:}")
    private String googleClientId;

    @Value("${GOOGLE_CLIENT_SECRET:}")
    private String googleClientSecret;

    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String CALENDAR_SCOPE = "https://www.googleapis.com/auth/calendar.events";

    private final ConcurrentHashMap<String, String> stateStore = new ConcurrentHashMap<>();

    @Override
    public Map<String, String> getAuthorizationUrl(String userEmail){
        String state = UUID.randomUUID().toString();
        stateStore.put(state, userEmail);

        String redirectUri = "http://localhost:2025/api/v1/google/calendar/callback";

        String authUrl = GOOGLE_AUTH_URL +
                "?client_id=" + googleClientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=" + CALENDAR_SCOPE +
                "&state=" + state +
                "&access_type=offline" +
                "&prompt=consent";

        return Map.of("authUrl", authUrl, "state", state);
    }

    @Override
    public void handleCallback(String code, String state){
        String userEmail = stateStore.remove(state);
        if(userEmail == null)
            throw new RuntimeException("Invalid or expired state parameter");

        UserEntity user = userRepository.findByEmail(userEmail);
        if(user == null)
            throw new RuntimeException("User not found: "+userEmail);

        String redirectUri = "http://localhost:2025/api/v1/google/calendar/callback";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "code="+code+
                "&client_code="+googleClientId+
                "&client_secret="+googleClientSecret+
                "&grant_type=authorization_code";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .uri(GOOGLE_TOKEN_URL)
                .body(request)
                .retrieve()
                .body(Map.class);


        if(response == null || !response.containsKey("access_token"))
            throw new RuntimeException("Failed to obtain access token from google");

        String accessToken = (String) response.get("access_token");
        String refreshToken = (String) response.get("refresh_token");
        Long expiresIn = response.get("expires_in") instanceof Number?((Number) response.get("expires_in")).longValue() : 3600L;

        if(accessToken == null)
            throw new RuntimeException("No access token in google response");

        GoogleCalenderCredentialEntity credential = googleCalenderCredentialRepository.findByUser(user)
                .orElseGet(() -> GoogleCalenderCredentialEntity.builder()
                        .user(user)
                        .build());

        credential.setAccessToken(accessToken);
        credential.setRefreshToken(refreshToken);
        credential.setExpiresAt(Instant.now().plusSeconds(expiresIn));
        credential.setConnected(true);

        googleCalenderCredentialRepository.save(credential);

        logger.info("[CalendarAuthService] Google Calendar connected for user email={}", userEmail);
    }
}

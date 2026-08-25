package com.proj.slotify.service;

import com.proj.slotify.entity.BookingEntity;
import com.proj.slotify.entity.GoogleCalenderCredentialEntity;
import com.proj.slotify.entity.UserEntity;
import com.proj.slotify.repository.GoogleCalenderCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

//import java.net.http.HttpHeaders;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleCalenderServiceImpl implements GoogleCalenderService{

    private static final Logger logger = LoggerFactory.getLogger(GoogleCalenderServiceImpl.class);
    private final GoogleCalenderCredentialRepository googleCalenderCredentialRepository;
    private final RestClient restClient = RestClient.create();

    @Value("${GOOGLE_CLIENT_ID:}")
    private String googleClientId;

    @Value("${GOOGLE_CLIENT_SECRET:}")
    private String googleClientSecret;

    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_CALENDER_API_URL = "https://www.googleapis.com/calendar/v3/calendars/primary/events";

    @Override
    public void createBookingEvent(BookingEntity booking){
        UserEntity host = booking.getOwner();
        logger.info("[GoogleCalendarService] Creating calendar event for bookingId={}, ownerId={}",
                booking.getBookingId(), host.getId());
        GoogleCalenderCredentialEntity credential = googleCalenderCredentialRepository.findByUser(host).orElse(null);

        if(credential == null){
            logger.warn("[GoogleCalendarService] No Google Calendar credentials found for user id={}. Skipping event creation.", host.getId());
            return;
        }

        try{
            //Ensure we have a valid access token
            String accessToken = ensureValidAccessToken(credential);

            //Create the calender event
            createCalenderEvent(accessToken, booking);

            logger.info("[GoogleCalendarService] Successfully created calendar event for bookingId={}", booking.getBookingId());
        } catch (Exception e) {
            logger.error("[GoogleCalendarService] Failed to create calendar event for bookingId={}", booking.getBookingId(), e);
        }
    }

    private String ensureValidAccessToken(GoogleCalenderCredentialEntity credential) {
        Instant now = Instant.now();

        // If token is still valid (with 5-minute buffer), use it as-is
        if (credential.getExpiresAt().isAfter(now.plusSeconds(300))) {
            logger.debug("[GoogleCalenderService] Access token still valid for user id={}", credential.getUser().getId());
            return credential.getAccessToken();
        }

        // Token expired — refresh it
        logger.info("[GoogleCalenderService] Access token expired for user id={}. Refreshing...", credential.getUser().getId());
        return refreshAccessToken(credential);
    }

    private String refreshAccessToken(GoogleCalenderCredentialEntity credential){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String body = "client_id="+googleClientId+
                    "&client_secret="+googleClientSecret+
                    "&refresh_token="+credential.getRefreshToken()+
                    "&grant_type=refresh_token";

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri(GOOGLE_TOKEN_URL)
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            if(response == null || !response.containsKey("access_token")){
                throw new RuntimeException("Failed to refresh access token: no access_token in response");
            }

            String newAccessToken = (String) response.get("access_token");
            Long expiresIn = response.get("expires_in") instanceof Number ? ((Number) response.get("expires_in")).longValue() : 3600L;

            //Update with new token
            credential.setAccessToken(newAccessToken);
            credential.setExpiresAt(Instant.now().plusSeconds(expiresIn));
            googleCalenderCredentialRepository.save(credential);

            logger.info("[GoogleCalendarService] Access token refreshed successfully for user id={}", credential.getUser().getId());
            return newAccessToken;
        }
        catch (Exception e){
            logger.error("[GoogleCalendarService] Failed to refresh access token for user id={}", credential.getUser().getId(), e);
            throw new RuntimeException("Failed to refresh Google Calender access token", e);
        }
    }

    private void createCalenderEvent(String accessToken, BookingEntity booking){
        try{
            Map<String, Object> event = Map.of(
                    "summary", "Slotify Booking - " + booking.getBookingId(),
                    "description", "Appointment booked through Slotify\nGuest: "+booking.getGuestName()+"\nEmail: "+booking.getGuestEmail(),
                    "start", Map.of(
                            "dateTime", booking.getStartTime().toString(),
                            "timeZone", booking.getOwner().getTimezone()
                    ),
                    "end", Map.of(
                            "dateTime", booking.getEndTime().toString(),
                            "timeZone", booking.getOwner().getTimezone()
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(event, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri(GOOGLE_CALENDER_API_URL)
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            if(response != null && response.containsKey("id")){
                logger.info("[GoogleCalendarService] Event created in Google Calendar: eventId={}", response.get("id"));
            }
            else{
                logger.warn("[GoogleCalendarService] Google Calendar API returned unexpected response: {}", response);
            }
        } catch (Exception e) {
            logger.error("[GoogleCalendarService] Failed to create calendar event via Google Calendar API", e);
            throw e;
        }
    }
}

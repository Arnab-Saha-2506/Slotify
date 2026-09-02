package com.proj.slotify.controller;

import com.proj.slotify.service.CalenderAuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/google/calendar")
public class CalenderAuthController {

    private final CalenderAuthService calenderAuthService;
    private static final Logger logger = LoggerFactory.getLogger(CalenderAuthController.class);

    @GetMapping("/connect")
    public ResponseEntity<Map<String, String>> connect(){
        String email = (String) org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        logger.info("[CalendarAuthController] Connect requested for email={}", email);

        Map<String, String> response = calenderAuthService.getAuthorizationUrl(email);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestParam String code, @RequestParam String state){
        logger.info("[CalendarAuthController] Callback received: state={}", state);

        try{
            calenderAuthService.handleCallback(code, state);
            return ResponseEntity.ok("Google calendar connected successfully!");
        } catch (Exception e) {
            logger.error("[CalendarAuthController] Calendar callback failed", e);
            return ResponseEntity.badRequest().body("Calendar connection failed: "+e.getMessage());
        }
    }
}

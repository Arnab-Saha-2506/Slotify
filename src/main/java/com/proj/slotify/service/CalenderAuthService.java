package com.proj.slotify.service;

import java.util.Map;

public interface CalenderAuthService {

    Map<String, String> getAuthorizationUrl(String userEmail);

    void handleCallback(String code, String state);
}

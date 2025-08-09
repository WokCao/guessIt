package com.FoZ.guessIt.Services;

import com.FoZ.guessIt.DTOs.GoogleUserInfoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;

@Service
public class GoogleService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${google.clientId}")
    private String GOOGLE_CLIENT_ID;
    @Value("${google.clientSecret}")
    private String GOOGLE_CLIENT_SECRET;
    @Value("${google.redirectUri}")
    private String REDIRECT_URI;
    @Autowired
    private ExternalService externalService;

    public Optional<GoogleUserInfoDTO> getGoogleInfo(String accessToken) {
        try {
            String responseBody = externalService.getGoogleInfo(accessToken);
            if (responseBody == null) {
                return Optional.empty();
            }

            // Parse JSON response
            GoogleUserInfoDTO googleUserInfoDTO = objectMapper.readValue(responseBody, GoogleUserInfoDTO.class);
            return Optional.ofNullable(googleUserInfoDTO);

        } catch (HttpClientErrorException e) {
            System.err.println("Google API client error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            System.err.println("Failed to parse Google user info: " + e.getMessage());
        } catch (RestClientException e) {
            System.err.println("Error calling Google API: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error fetching Google user info: " + e.getMessage());
        }
        return Optional.empty();
    }

    public GoogleTokenResponse getGoogleToken(String authCode) {
        try {
            return new GoogleAuthorizationCodeTokenRequest(
                    new com.google.api.client.http.javanet.NetHttpTransport(),
                    com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
                    GOOGLE_CLIENT_ID,
                    GOOGLE_CLIENT_SECRET,
                    authCode,
                    REDIRECT_URI
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

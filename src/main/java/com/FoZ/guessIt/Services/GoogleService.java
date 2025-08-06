package com.FoZ.guessIt.Services;

import com.FoZ.guessIt.DTOs.GoogleUserInfoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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

    public Optional<GoogleUserInfoDTO> getGoogleInfo(String accessToken) {
        String userInfoEndpoint = "https://www.googleapis.com/oauth2/v2/userinfo";

        try {
            // Create request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Make the API call
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoEndpoint,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            // Verify successful response
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("Google API returned non-success status: " + response.getStatusCode());
            }

            // Parse JSON response
            GoogleUserInfoDTO googleUserInfoDTO = objectMapper.readValue(response.getBody(), GoogleUserInfoDTO.class);
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

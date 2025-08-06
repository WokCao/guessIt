package com.FoZ.guessIt.Services;

import com.FoZ.guessIt.DTOs.GoogleUserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class GoogleInfoService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate;

    public Optional<GoogleUserInfo> getGoogleInfo(String accessToken) {
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
            GoogleUserInfo userInfo = objectMapper.readValue(response.getBody(), GoogleUserInfo.class);
            return Optional.ofNullable(userInfo);

        } catch (HttpClientErrorException e) {
            System.err.println("Google API client error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return Optional.empty();
        } catch (JsonProcessingException e) {
            System.err.println("Failed to parse Google user info: " + e.getMessage());
            return Optional.empty();
        } catch (RestClientException e) {
            System.err.println("Error calling Google API: " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Unexpected error fetching Google user info: " + e.getMessage());
            return Optional.empty();
        }
    }
}

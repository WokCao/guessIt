package com.FoZ.guessIt.Services;

import com.FoZ.guessIt.DTOs.FacebookUserInfoDTO;
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
public class FacebookService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate;

    public Optional<FacebookUserInfoDTO> getFacebookUserInfo(String accessToken) {
        try {
            String url = "https://graph.facebook.com/v23.0/me?fields=id,name,email,picture,birthday&access_token=" + accessToken;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                FacebookUserInfoDTO userInfo = objectMapper.readValue(response.getBody(), FacebookUserInfoDTO.class);
                return Optional.ofNullable(userInfo);
            } else {
                System.err.println("Facebook API returned non-success status: " + response.getStatusCode());
                return Optional.empty();
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Facebook API client error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (JsonProcessingException e) {
            System.err.println("Error parsing Facebook user info response: " + e.getMessage());
        } catch (RestClientException e) {
            System.err.println("Error calling Facebook API: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error fetching Facebook user info: " + e.getMessage());
        }
        return Optional.empty();
    }

}

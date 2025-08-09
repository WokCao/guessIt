package com.FoZ.guessIt.Services;

import com.FoZ.guessIt.DTOs.SynAntDTO;
import com.FoZ.guessIt.Enumerations.Quantity;
import com.FoZ.guessIt.Enumerations.RelationType;
import com.FoZ.guessIt.Models.DictionaryEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ExternalService {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${external.dictionary.api.url}")
    private String dictionaryAPIUrl;
    @Value("${google.userinfo.endpoint}")
    private String userInfoEndpoint;
    @Value("${external.data-muse.api.url}")
    private String dataMuseAPIUrl;

    public Optional<DictionaryEntry[]> getDictionaryEntry(String word) {
        String url = dictionaryAPIUrl + "/" + word;
        try {
            DictionaryEntry[] dictionaryEntries = restTemplate.getForObject(
                    url,
                    DictionaryEntry[].class
            );

            if (dictionaryEntries == null) {
                Optional<String> bestMatch = getBestMatch(word);
                if (bestMatch.isPresent()) {
                    return getDictionaryEntry(bestMatch.get());
                }
                return Optional.empty();
            }

            return Optional.of(dictionaryEntries);
        } catch (HttpClientErrorException e) {
            System.err.println("Error calling external API: " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> getBestMatch(String word) {
        String url = dataMuseAPIUrl + "/sug?s=" + word;
        try {
            SynAntDTO[] bestMatches = restTemplate.getForObject(
                    url,
                    SynAntDTO[].class
            );

            if (bestMatches == null) {
                return Optional.empty();
            }

            for (SynAntDTO bestMatch : bestMatches) {
                if (bestMatch.getWord().startsWith(word.trim() + " ")) {
                    return Optional.of(bestMatch.getWord());
                }
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error calling external API: " + e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<SynAntDTO[]> getDictionaryEntryRelated(RelationType relationType, String word, Quantity quantity) {
        int limit = quantity.getValue();

        if (limit > 1000) {
            limit = 1000;
        }

        if (limit < 1) {
            limit = 1;
        }

        String relationParam = switch (relationType) {
            case SYNONYM -> "rel_syn";
            case ANTONYM -> "rel_ant";
        };

        String url = String.format("%s/words?%s=%s&max=%d&md=p", dataMuseAPIUrl, relationParam, word, limit);

        try {
            SynAntDTO[] relatedWords = restTemplate.getForObject(
                    url,
                    SynAntDTO[].class
            );

            if (relatedWords == null) {
                return Optional.empty();
            }

            for (SynAntDTO relatedWord : relatedWords) {
                if (relatedWord.getScore() == null) {
                    relatedWord.setScore(0L);
                }
            }
            return Optional.of(relatedWords);

        } catch (HttpClientErrorException e) {
            System.err.println("Error calling external API: " + e.getMessage());
        }

        return Optional.empty();
    }

    public String getGoogleInfo(String accessToken) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
                userInfoEndpoint,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("Google API returned non-success status: " + response.getStatusCode());
        }

        return response.getBody();
    }
}

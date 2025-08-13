package com.FoZ.guessIt.Services;

import com.FoZ.guessIt.DTOs.IncompleteDictionaryEntryDTO;
import com.FoZ.guessIt.DTOs.SynAntDTO;
import com.FoZ.guessIt.Enumerations.Quantity;
import com.FoZ.guessIt.Enumerations.RelationType;
import com.FoZ.guessIt.Models.Definition;
import com.FoZ.guessIt.Models.DictionaryEntry;
import com.FoZ.guessIt.Models.Meaning;
import com.FoZ.guessIt.Models.Phonetic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
        String url = String.format("%s/%s", dictionaryAPIUrl, word);
        try {
            if (Objects.equals(word, "lordliness")) {
                return Optional.empty();
            }
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

    public Optional<DictionaryEntry> getIncompleteDictionaryEntry(String word) {
        String url = String.format("%s/words?sp=%s&md=pdr", dataMuseAPIUrl, word);
        try {
            IncompleteDictionaryEntryDTO[] suggestions = restTemplate.getForObject(
                    url,
                    IncompleteDictionaryEntryDTO[].class
            );

            if (suggestions == null) {
                return Optional.empty();
            }

            for (IncompleteDictionaryEntryDTO suggestion : suggestions) {
                if (suggestion.getWord().equals(word)) {
                    return Optional.ofNullable(dictionaryEntryBuilder(suggestion));
                }
            }

            return Optional.empty();
        } catch (HttpClientErrorException e) {
            System.err.println("Error calling external API: " + e.getMessage());
            return Optional.empty();
        }
    }

    private DictionaryEntry dictionaryEntryBuilder(IncompleteDictionaryEntryDTO suggestion) {
        String pronExtraction = suggestion.getTags().getLast();
        String phonetic = IPAConvertService.convertArpabetToIpa(pronExtraction.substring(5));
        List<Phonetic> phonetics = new ArrayList<>(Collections.singleton(new Phonetic(phonetic, null)));
        List<Meaning> meanings = new LinkedList<>();

        for (String partOfSpeech : suggestion.getTags()) {
            Meaning meaning = new Meaning();
            switch (partOfSpeech) {
                case "n":
                    meaning.setPartOfSpeech("noun");
                    break;
                case "v":
                    meaning.setPartOfSpeech("verb");
                    break;
                case "adj":
                    meaning.setPartOfSpeech("adjective");
                    break;
                case "adv":
                    meaning.setPartOfSpeech("adverb");
                    break;
                default:
                    break;
            }

            if (suggestion.getDefs() == null) {
                return null;
            }

            for (String def : suggestion.getDefs()) {
                Definition definition = new Definition();
                if (def.contains("obsolete")) {
                    continue;
                }

                if (def.contains("n\t") && partOfSpeech.equals("n")) {
                    String newDef = def.replace("n\t", "");
                    definition.setDefinition(newDef);
                    definition.setExample(null);
                } else if (def.contains("v\t") && partOfSpeech.equals("v")) {
                    String newDef = def.replace("v\t", "");
                    definition.setDefinition(newDef);
                    definition.setExample(null);
                } else if (def.contains("adj\t") && partOfSpeech.equals("adj")) {
                    String newDef = def.replace("adj\t", "");
                    definition.setDefinition(newDef);
                    definition.setExample(null);
                } else if (def.contains("adv\t") && partOfSpeech.equals("adv")) {
                    String newDef = def.replace("adv\t", "");
                    definition.setDefinition(newDef);
                    definition.setExample(null);
                } else {
                    definition.setDefinition(null);
                    definition.setExample(null);
                }

                if (definition.getDefinition() != null) {
                    meaning.getDefinitions().add(definition);
                }
            }

            if (!meaning.getDefinitions().isEmpty() && meaning.getPartOfSpeech() != null) {
                meanings.add(meaning);
            }
        }


        try {
            DictionaryEntry dictionaryEntry = new DictionaryEntry();
            dictionaryEntry.setWord(suggestion.getWord());
            dictionaryEntry.setPhonetic(phonetic);
            dictionaryEntry.setPhonetics(phonetics);
            dictionaryEntry.setMeanings(meanings);
            return dictionaryEntry;
        } catch (Exception e) {
            System.err.println("Error building dictionary entry: " + e.getMessage());
            return null;
        }
    }

    public Optional<String> getBestMatch(String word) {
        String url = String.format("%s/sug?s=%s", dataMuseAPIUrl, word);
        try {
            SynAntDTO[] bestMatches = restTemplate.getForObject(
                    url,
                    SynAntDTO[].class
            );

            if (bestMatches == null) {
                return Optional.empty();
            }

            for (SynAntDTO bestMatch : bestMatches) {
                if (bestMatch.getWord().startsWith(word + " ")) {
                    return Optional.of(bestMatch.getWord());
                }
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error calling external API: " + e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<SynAntDTO[]> getSuggestions(String word, Long quantity) {
        String url = String.format("%s/sug?s=%s&max=%d", dataMuseAPIUrl, word, quantity);
        try {
            SynAntDTO[] suggestions = restTemplate.getForObject(
                    url,
                    SynAntDTO[].class
            );

            return Optional.ofNullable(suggestions);
        } catch (HttpClientErrorException e) {
            System.err.println("Error calling external API: " + e.getMessage());
            return Optional.empty();
        }
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

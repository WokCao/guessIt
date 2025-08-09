package com.FoZ.guessIt.Services;

import com.FoZ.guessIt.DTOs.SynAntDTO;
import com.FoZ.guessIt.Enumerations.Quantity;
import com.FoZ.guessIt.Enumerations.RelationType;
import com.FoZ.guessIt.Models.DictionaryEntry;
import com.FoZ.guessIt.Models.Meaning;
import com.FoZ.guessIt.Respositories.DictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class DictionaryService {
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private ExternalService externalService;

    public Optional<DictionaryEntry> getWordDetails(String query) {
        Optional<DictionaryEntry> existingEntry = dictionaryRepository.findByWord(query.toLowerCase());
        
        /* If the word already exists in the database, return it */
        if (existingEntry.isPresent()) {
            return existingEntry;
        }

        /* If the word does not exist in the database, call the external API */
        Optional<DictionaryEntry[]> externalDataWrapper = externalService.getDictionaryEntry(query);

        if (externalDataWrapper.isPresent()) {
            DictionaryEntry entry = getDictionaryEntry(externalDataWrapper.get());
            Optional<SynAntDTO[]> synonyms = externalService.getDictionaryEntryRelated(RelationType.SYNONYM, query, Quantity.DEFAULT);
            Optional<SynAntDTO[]> antonyms = externalService.getDictionaryEntryRelated(RelationType.ANTONYM, query, Quantity.DEFAULT);

            if (synonyms.isPresent()) {
                List<SynAntDTO> synonymsList = List.of(synonyms.get());
                Long averageScore = getAverageScore(synonymsList);
                List<String> synonymsStringList = synonymsList.stream()
                        .filter(synAnt -> synAnt.getScore() >= averageScore)
                        .limit(5)
                        .map(SynAntDTO::getWord)
                        .toList();
                entry.setSynonyms(synonymsStringList);
            }

            if (antonyms.isPresent()) {
                List<SynAntDTO> antonymsList = List.of(antonyms.get());
                Long averageScore = getAverageScore(antonymsList);
                List<String> antonymsStringList = antonymsList.stream()
                        .filter(synAnt -> synAnt.getScore() >= averageScore)
                        .limit(5)
                        .map(SynAntDTO::getWord)
                        .toList();
                entry.setAntonyms(antonymsStringList);
            }

            dictionaryRepository.save(entry);
            
            return Optional.of(entry);
        }
        
        return Optional.empty();
    }

    private DictionaryEntry getDictionaryEntry(DictionaryEntry[] externalData) {
        /* Merge dictionary entry by meaning (with same part of speech) */
        DictionaryEntry entry = new DictionaryEntry();
        entry.setWord(externalData[0].getWord());
        entry.setPhonetic(externalData[0].getPhonetic());
        entry.setPhonetics(externalData[0].getPhonetics());

        List<Meaning> allMeanings = new LinkedList<>();
        for (DictionaryEntry externalEntry : externalData) {
            for (Meaning externalMeaning : externalEntry.getMeanings()) {
                boolean meaningFound = false;
                for (Meaning meaning : allMeanings) {
                    if (meaning.getPartOfSpeech().equals(externalMeaning.getPartOfSpeech())) {
                        meaningFound = true;
                        meaning.getDefinitions().addAll(externalMeaning.getDefinitions());
                        break;
                    }
                }
                if (!meaningFound) {
                    allMeanings.add(externalMeaning);
                }
            }
        }
        entry.setMeanings(allMeanings);
        return entry;
    }

    private Long getAverageScore(List<SynAntDTO> synAnts) {
        if (synAnts.isEmpty()) {
            return 0L;
        }
        long sum = 0;
        for (SynAntDTO synAnt : synAnts) {
            sum += synAnt.getScore();
        }
        return sum / synAnts.size();
    }
}

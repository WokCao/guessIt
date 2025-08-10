package com.FoZ.guessIt.DTOs;

import com.FoZ.guessIt.Models.DictionaryEntry;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter @AllArgsConstructor
public class DictionaryEntryResponseDTO {
    @JsonProperty("isFound")
    private boolean isFound;
    private DictionaryEntry dictionaryEntry;
    private List<String> suggestions;
}

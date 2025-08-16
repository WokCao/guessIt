package com.FoZ.guessIt.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddWordToCollectionDTO {
    @NotNull
    private List<Long> collectionIds;
    @NotNull
    private Long dictionaryEntryId;
}

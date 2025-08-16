package com.FoZ.guessIt.DTOs;

import com.FoZ.guessIt.Models.CollectionWord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionWordDTO {

    private Long id;
    private Long dictionaryEntryId;
    private int lastestAccuracy;
    private boolean starMarked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CollectionWordDTO(CollectionWord cw) {
        this.id = cw.getId();
        this.dictionaryEntryId = cw.getDictionaryEntry().getId();
        this.lastestAccuracy = cw.getLastestAccuracy();
        this.starMarked = cw.isStarMarked();
        this.createdAt = cw.getCreatedAt();
        this.updatedAt = cw.getUpdatedAt();
    }
}
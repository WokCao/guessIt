package com.FoZ.guessIt.DTOs;

import com.FoZ.guessIt.Enumerations.Difficulty;
import com.FoZ.guessIt.Enumerations.Visibility;
import com.FoZ.guessIt.Models.CollectionModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Difficulty difficultyLevel;
    private String topic;
    private Visibility mode;
    private Long likes;
    private Long joinedCollection;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CollectionWordDTO> words;
    private UserDTO user;

    public CollectionResponseDTO(CollectionModel collection) {
        this.id = collection.getId();
        this.name = collection.getName();
        this.description = collection.getDescription();
        this.difficultyLevel = collection.getDifficultyLevel();
        this.topic = collection.getTopic();
        this.mode = collection.getMode();
        this.likes = collection.getLikes();
        this.joinedCollection = collection.getJoinedCollection();
        this.createdAt = collection.getCreatedAt();
        this.updatedAt = collection.getUpdatedAt();
        this.words = collection.getWords()
                .stream()
                .map(CollectionWordDTO::new)
                .toList();
        this.user = new UserDTO(collection.getUserModel());
    }
}
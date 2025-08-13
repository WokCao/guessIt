package com.FoZ.guessIt.DTOs;

import com.FoZ.guessIt.Enumerations.Difficulty;
import com.FoZ.guessIt.Enumerations.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CollectionDTO {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Difficulty level is required")
    private Difficulty difficultyLevel;

    @NotNull(message = "Topic is required")
    private String topic;

    @NotNull(message = "Mode is required")
    private Visibility mode;
}

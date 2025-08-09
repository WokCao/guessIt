package com.FoZ.guessIt.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Definition {
    @Column(columnDefinition = "TEXT")
    private String definition;
    private String example;
}

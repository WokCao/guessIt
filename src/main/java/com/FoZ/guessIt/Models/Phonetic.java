package com.FoZ.guessIt.Models;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Phonetic {
    private String text;
    private String audio;
}

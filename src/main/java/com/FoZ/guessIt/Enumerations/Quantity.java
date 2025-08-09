package com.FoZ.guessIt.Enumerations;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum Quantity {
    MIN(1),
    DEFAULT(10),
    STANDARD(100),
    MEDIUM(250),
    HALF(500),
    MAX(1000);

    private final int value;
}
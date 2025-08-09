package com.FoZ.guessIt.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meanings")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Meaning {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String partOfSpeech;

    @ElementCollection
    @CollectionTable(name = "definitions", joinColumns = @JoinColumn(name = "meaning_id"))
    private List<Definition> definitions = new ArrayList<>();
}

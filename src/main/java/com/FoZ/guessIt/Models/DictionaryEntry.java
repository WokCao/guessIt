package com.FoZ.guessIt.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dictionary_entry")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DictionaryEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;
    private String phonetic;

    @ElementCollection
    @CollectionTable(name = "phonetics", joinColumns = @JoinColumn(name = "entry_id"))
    private List<Phonetic> phonetics = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "entry_id")
    private List<Meaning> meanings = new ArrayList<>();


    @ElementCollection
    @CollectionTable(name = "synonyms", joinColumns = @JoinColumn(name = "entry_id"))
    @Column(name = "synonym", columnDefinition = "TEXT")
    private List<String> synonyms = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "antonyms", joinColumns = @JoinColumn(name = "entry_id"))
    @Column(name = "antonym", columnDefinition = "TEXT")
    private List<String> antonyms = new ArrayList<>();
}


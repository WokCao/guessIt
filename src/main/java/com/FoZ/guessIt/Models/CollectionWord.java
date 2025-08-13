package com.FoZ.guessIt.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "collection_words")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CollectionWord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id", nullable = false)
    @ToString.Exclude
    private CollectionModel collection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dictionary_entry_id", nullable = false)
    private DictionaryEntry dictionaryEntry;

    @Column(name = "lastest_accuracy")
    private int lastestAccuracy = 0;

    @Column(name = "star_marked")
    private boolean starMarked = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
package com.FoZ.guessIt.Repositories;

import com.FoZ.guessIt.Models.DictionaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DictionaryRepository extends JpaRepository<DictionaryEntry, Long> {
    Optional<DictionaryEntry> findByWord(String word);
}

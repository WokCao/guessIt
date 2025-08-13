package com.FoZ.guessIt.Repositories;

import com.FoZ.guessIt.Enumerations.Difficulty;
import com.FoZ.guessIt.Enumerations.Visibility;
import com.FoZ.guessIt.Models.CollectionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<CollectionModel, Long> {
    /* Find by mode and difficulty with user id */
    Page<CollectionModel> findByModeAndDifficultyLevelAndUserModel_Id(Visibility mode, Difficulty difficulty, Long userId, Pageable pageable);

    /* Find by mode and user id */
    Page<CollectionModel> findByModeAndUserModel_Id(Visibility mode, Long userId, Pageable pageable);

    /* Find by difficulty and user id */
    Page<CollectionModel> findByDifficultyLevelAndUserModel_Id(Difficulty difficulty, Long userId, Pageable pageable);

    /* Find by user id */
    Page<CollectionModel> findByUserModel_Id(Long userId, Pageable pageable);

    /* Find popular collections (Public mode only + sort by score based on likes (60%) and joined users (40%)) */
    @Query("SELECT c FROM CollectionModel c WHERE c.mode = :mode ORDER BY (c.likes * 0.6 + c.joinedCollection * 0.4) DESC")
    Page<CollectionModel> findPopularByMode(@Param("mode") Visibility mode, Pageable pageable);
}

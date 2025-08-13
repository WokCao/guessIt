package com.FoZ.guessIt.Services;

import com.FoZ.guessIt.DTOs.CollectionDTO;
import com.FoZ.guessIt.Enumerations.Difficulty;
import com.FoZ.guessIt.Enumerations.Visibility;
import com.FoZ.guessIt.Models.CollectionModel;
import com.FoZ.guessIt.Models.UserModel;
import com.FoZ.guessIt.Repositories.CollectionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CollectionService {
    @Autowired
    private CollectionRepository collectionRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private JwtService jwtService;

    public CollectionModel createCollection(CollectionDTO collectionDTO, String jwt) throws Exception {
        Long userId = jwtService.getUserIdFromJWT(jwt);
        /* Find user by id */
        UserModel user = entityManager.find(UserModel.class, userId);

        CollectionModel collectionModel = new CollectionModel();
        collectionModel.setName(collectionDTO.getName());
        collectionModel.setDescription(collectionDTO.getDescription());
        collectionModel.setDifficultyLevel(collectionDTO.getDifficultyLevel());
        collectionModel.setTopic(collectionDTO.getTopic());
        collectionModel.setMode(collectionDTO.getMode());
        collectionModel.setUserModel(user);

        return collectionRepository.save(collectionModel);
    }

    public Page<CollectionModel> getCollections(int limit, int offset, Visibility visibility, Difficulty difficultyLevel, boolean isPopular, String jwt) throws Exception {
        Long userId = jwtService.getUserIdFromJWT(jwt);

        /* Check if limit is between 6 and 100 */
        if (limit < 6 || limit > 100) {
            limit = 6;
        }

        Pageable pageable = PageRequest.of((int) offset / limit, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (!isPopular) {
            /* Get collections by user id */
            if (visibility == null && difficultyLevel == null) {
                return collectionRepository.findByUserModel_Id(userId, pageable);
            } else if (visibility == null) {
                return collectionRepository.findByDifficultyLevelAndUserModel_Id(difficultyLevel, userId, pageable);
            } else if (difficultyLevel == null) {
                return collectionRepository.findByModeAndUserModel_Id(visibility, userId, pageable);
            } else {
                return collectionRepository.findByModeAndDifficultyLevelAndUserModel_Id(visibility, difficultyLevel, userId, pageable);
            }
        } else {
            /* Get popular collections */
            return collectionRepository.findPopularByMode(visibility, pageable);
        }
    }
}

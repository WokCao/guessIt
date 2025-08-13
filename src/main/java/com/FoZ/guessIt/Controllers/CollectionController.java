package com.FoZ.guessIt.Controllers;

import com.FoZ.guessIt.DTOs.CollectionDTO;
import com.FoZ.guessIt.DTOs.CollectionResponseDTO;
import com.FoZ.guessIt.Enumerations.Difficulty;
import com.FoZ.guessIt.Enumerations.Visibility;
import com.FoZ.guessIt.Models.CollectionModel;
import com.FoZ.guessIt.Services.CollectionService;
import com.FoZ.guessIt.Services.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/collection")
public class CollectionController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CollectionService collectionService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("")
    public ResponseEntity<?> createNewCollection(
            @Valid @RequestBody CollectionDTO collectionDTO,
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;

        if (!jwtService.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        try {
            CollectionModel savedCollection = collectionService.createCollection(collectionDTO, jwt);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCollection);
        } catch (Exception e) {
            System.err.println("Error processing request" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<?> getCollections(
            @RequestParam(value = "limit", defaultValue = "6", required = false) int limit,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "mode", required = false) Visibility mode,
            @RequestParam(value = "difficultyLevel", required = false) Difficulty difficultyLevel,
            @RequestParam(value = "isPopular", defaultValue = "false", required = false) boolean isPopular,
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;

        if (!jwtService.validateToken(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        try {
            Page<CollectionModel> collections = collectionService.getCollections(limit, offset, mode, difficultyLevel, isPopular, jwt);
            return ResponseEntity.ok(collections.map(CollectionResponseDTO::new));
        } catch (Exception e) {
            System.err.println("Error processing request" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
        }
    }
}
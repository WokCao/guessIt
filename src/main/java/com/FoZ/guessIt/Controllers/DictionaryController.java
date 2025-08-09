package com.FoZ.guessIt.Controllers;

import com.FoZ.guessIt.Services.DictionaryService;
import com.FoZ.guessIt.Services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dictionary")
public class DictionaryController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private DictionaryService dictionaryService;

    @GetMapping("/{query}")
    public ResponseEntity<?> getQuery(@PathVariable("query") String query, @RequestHeader("Authorization") String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        if (!jwtService.validateToken(jwtToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        try {
            return ResponseEntity.ok(dictionaryService.getWordDetails(query));
        } catch (Exception e) {
            System.out.println("Error processing request" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
        }
    }
}

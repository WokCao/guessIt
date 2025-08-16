package com.FoZ.guessIt.Controllers;

import com.FoZ.guessIt.DTOs.EmailLoginDTO;
import com.FoZ.guessIt.DTOs.LoginResponseDTO;
import com.FoZ.guessIt.Models.UserModel;
import com.FoZ.guessIt.Services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/email-login")
    public ResponseEntity<?> emailLogin(@RequestBody @Valid EmailLoginDTO emailLoginDTO) {
        UserModel userModel = authenticationService.validateUserCredentials(emailLoginDTO.getEmail(), emailLoginDTO.getPassword());
        if (userModel == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        String jwt = authenticationService.authenticateWithUsernamePassword(emailLoginDTO);

        return ResponseEntity.ok(new LoginResponseDTO(jwt, userModel));
    }

    @PostMapping("/google")
    public ResponseEntity<?> google(@RequestBody Map<String, String> body) {
        String authCode = body.get("authCode");
        if (authCode == null) {
            return ResponseEntity.badRequest().body("Auth code not provided");
        }
         try {
             UserModel userModel = authenticationService.validateThirdPartyUser(authCode, null);
             log.info("User: {}", userModel);
             String jwt = authenticationService.authenticateWithThirdParty(userModel);
             return ResponseEntity.ok(new LoginResponseDTO(jwt, userModel));
         } catch (BadCredentialsException | BadRequestException e) {
             return ResponseEntity.badRequest().body(e.getMessage());
         } catch (Exception e) {
             System.out.println(e.getMessage());
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
         }
    }

    @PostMapping("/facebook")
    public ResponseEntity<?> facebook(@RequestBody Map<String, String> body) {
        String accessToken = body.get("accessToken");
        if (accessToken == null) {
            return ResponseEntity.badRequest().body("Access token not provided");
        }

        try {
            UserModel userModel = authenticationService.validateThirdPartyUser(null, accessToken);
            String jwt = authenticationService.authenticateWithThirdParty(userModel);
            return ResponseEntity.ok(new LoginResponseDTO(jwt, userModel));
        } catch (BadCredentialsException | BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestBody Map<String, String> body) {
        String jwt = body.get("accessToken");
        if (jwt == null) {
            return ResponseEntity.badRequest().body("JWT not provided");
        }

        try {
            return ResponseEntity.ok(authenticationService.validateToken(jwt));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing request");
        }
    }
}

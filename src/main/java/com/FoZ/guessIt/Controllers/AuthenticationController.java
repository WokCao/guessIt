package com.FoZ.guessIt.Controllers;

import com.FoZ.guessIt.DTOs.EmailLoginDTO;
import com.FoZ.guessIt.DTOs.GoogleUserInfo;
import com.FoZ.guessIt.Enumerations.AuthProvider;
import com.FoZ.guessIt.Models.UserModel;
import com.FoZ.guessIt.Respositories.UserRepository;
import com.FoZ.guessIt.Services.GoogleInfoService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GoogleInfoService googleInfoService;

    @Value("${google.clientId}")
    private String GOOGLE_CLIENT_ID;
    @Value("${google.clientSecret}")
    private String GOOGLE_CLIENT_SECRET;
    @Value("${google.redirectUri}")
    private String REDIRECT_URI;

    @PostMapping("/email-login")
    public ResponseEntity<?> emailLogin(@RequestBody @Valid EmailLoginDTO emailLoginDTO) {
        String email = emailLoginDTO.getEmail();
        String password = emailLoginDTO.getPassword();

        UserModel userModel = userRepository.findByEmail(email).orElse(null);
        if (userModel == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (!passwordEncoder.matches(password, userModel.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password");
        }

        return ResponseEntity.ok(userModel);
    }

    @PostMapping("/google")
    public ResponseEntity<?> google(@RequestBody Map<String, String> body) {
        String authCode = body.get("authCode");
        if (authCode == null) {
            return ResponseEntity.badRequest().body("Auth code not provided");
        }

        GoogleTokenResponse tokenResponse = null;
        try {
            tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new com.google.api.client.http.javanet.NetHttpTransport(),
                    com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
                    GOOGLE_CLIENT_ID,
                    GOOGLE_CLIENT_SECRET,
                    authCode,
                    REDIRECT_URI
            ).execute();

            Optional<GoogleUserInfo> googleUserInfoWrapper = googleInfoService.getGoogleInfo(tokenResponse.getAccessToken());
            if (googleUserInfoWrapper.isPresent()) {
                GoogleUserInfo googleUserInfo = googleUserInfoWrapper.get();
                Optional<UserModel> userModelWrapper = userRepository.findByProviderId(googleUserInfo.getId());
                Optional<UserModel> userModelWrapper1 = userRepository.findByEmail(googleUserInfo.getEmail());

                if (userModelWrapper.isPresent()) {
                    return ResponseEntity.ok(userModelWrapper.get());
                } else if (userModelWrapper1.isPresent()) {
                    return ResponseEntity.badRequest().body("Email %s is already in use".formatted(googleUserInfo.getEmail()));
                } else {
                    UserModel userModel = new UserModel();
                    userModel.setEmail(googleUserInfo.getEmail());
                    userModel.setRole("USER");
                    userModel.setProvider(AuthProvider.GOOGLE);
                    userModel.setProviderId(googleUserInfo.getId());
                    userModel.setImageUrl(googleUserInfo.getPicture());
                    userModel.setFullName(googleUserInfo.getName());
                    userModel.setAccountVerified(true);
                    userRepository.save(userModel);
                    return ResponseEntity.ok(userModel);
                }
            } else {
                return ResponseEntity.badRequest().body("Failed to fetch Google user info");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

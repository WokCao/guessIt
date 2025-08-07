package com.FoZ.guessIt.Controllers;

import com.FoZ.guessIt.DTOs.EmailLoginDTO;
import com.FoZ.guessIt.DTOs.FacebookUserInfoDTO;
import com.FoZ.guessIt.DTOs.GoogleUserInfoDTO;
import com.FoZ.guessIt.DTOs.LoginResponseDTO;
import com.FoZ.guessIt.Enumerations.AuthProvider;
import com.FoZ.guessIt.Models.UserModel;
import com.FoZ.guessIt.Respositories.UserRepository;
import com.FoZ.guessIt.Services.FacebookService;
import com.FoZ.guessIt.Services.GoogleService;
import com.FoZ.guessIt.Services.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    private GoogleService googleService;
    @Autowired
    private FacebookService facebookService;
    @Autowired
    private JwtService jwtService;

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

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password, List.of(new SimpleGrantedAuthority(userModel.getRole())));
        String jwt = jwtService.generateToken(authentication);

        return ResponseEntity.ok(new LoginResponseDTO(jwt, userModel));
    }

    @PostMapping("/google")
    public ResponseEntity<?> google(@RequestBody Map<String, String> body) {
        String authCode = body.get("authCode");
        if (authCode == null) {
            return ResponseEntity.badRequest().body("Auth code not provided");
        }

        GoogleTokenResponse tokenResponse = googleService.getGoogleToken(authCode);

        Optional<GoogleUserInfoDTO> googleUserInfoDTOWrapper = googleService.getGoogleInfo(tokenResponse.getAccessToken());
        if (googleUserInfoDTOWrapper.isPresent()) {
            GoogleUserInfoDTO googleUserInfoDTO = googleUserInfoDTOWrapper.get();
            Optional<UserModel> userModelWrapper = userRepository.findByProviderId(googleUserInfoDTO.getId());
            Optional<UserModel> userModelWrapper1 = userRepository.findByEmail(googleUserInfoDTO.getEmail());

            UserModel returnUserModel = null;

            if (userModelWrapper.isPresent()) {
                returnUserModel = userModelWrapper.get();
            } else if (userModelWrapper1.isPresent()) {
                return ResponseEntity.badRequest().body("Email %s is already in use".formatted(googleUserInfoDTO.getEmail()));
            } else {
                UserModel userModel = new UserModel(googleUserInfoDTO.getEmail(), googleUserInfoDTO.getName(), "USER", AuthProvider.GOOGLE, googleUserInfoDTO.getId(), googleUserInfoDTO.getPicture(), true);
                returnUserModel = userRepository.save(userModel);
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    returnUserModel.getEmail(),
                    null,
                    List.of(new SimpleGrantedAuthority(returnUserModel.getRole()))
            );
            String jwt = jwtService.generateToken(authentication);

            return ResponseEntity.ok(new LoginResponseDTO(jwt, returnUserModel));
        } else {
            return ResponseEntity.badRequest().body("Failed to fetch Google user info");
        }
    }

    @PostMapping("/facebook")
    public ResponseEntity<?> facebook(@RequestBody Map<String, String> body) {
        String accessToken = body.get("accessToken");
        if (accessToken == null) {
            return ResponseEntity.badRequest().body("Access token not provided");
        }

        Optional<FacebookUserInfoDTO> facebookUserInfoWrapper = facebookService.getFacebookUserInfo(accessToken);
        if (facebookUserInfoWrapper.isPresent()) {
            FacebookUserInfoDTO facebookUserInfoDTO = facebookUserInfoWrapper.get();
            Optional<UserModel> userModelWrapper = userRepository.findByProviderId(facebookUserInfoDTO.getId());
            Optional<UserModel> userModelWrapper1 = userRepository.findByEmail(facebookUserInfoDTO.getEmail());

            UserModel returnUserModel = null;

            if (userModelWrapper.isPresent()) {
                returnUserModel = userModelWrapper.get();
            } else if (userModelWrapper1.isPresent()) {
                return ResponseEntity.badRequest().body("Email %s is already in use".formatted(facebookUserInfoDTO.getEmail()));
            } else {
                UserModel userModel = new UserModel(facebookUserInfoDTO.getEmail(), facebookUserInfoDTO.getName(), "USER", AuthProvider.FACEBOOK, facebookUserInfoDTO.getId(), facebookUserInfoDTO.getPicture().getPictureData().getUrl(), true);
                returnUserModel = userRepository.save(userModel);
            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    returnUserModel.getEmail(),
                    null,
                    List.of(new SimpleGrantedAuthority(returnUserModel.getRole()))
            );
            String jwt = jwtService.generateToken(authentication);

            return ResponseEntity.ok(new LoginResponseDTO(jwt, returnUserModel));
        } else {
            return ResponseEntity.badRequest().body("Failed to fetch Facebook user info");
        }
    }
}

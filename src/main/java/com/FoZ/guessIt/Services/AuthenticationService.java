package com.FoZ.guessIt.Services;

import com.FoZ.guessIt.DTOs.EmailLoginDTO;
import com.FoZ.guessIt.DTOs.FacebookUserInfoDTO;
import com.FoZ.guessIt.DTOs.GoogleUserInfoDTO;
import com.FoZ.guessIt.Enumerations.AuthProvider;
import com.FoZ.guessIt.Models.GuessItUserDetails;
import com.FoZ.guessIt.Models.UserModel;
import com.FoZ.guessIt.Repositories.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private GoogleService googleService;
    @Autowired
    private FacebookService facebookService;

    public UserModel validateUserCredentials(String email, String rawPassword) throws BadCredentialsException {
        UserModel user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return user;
    }

    public String authenticateWithUsernamePassword(EmailLoginDTO emailLoginDTO) throws BadCredentialsException {
        UserModel user = validateUserCredentials(emailLoginDTO.getEmail(), emailLoginDTO.getPassword());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority(user.getRole()))
        );

        return jwtService.generateToken(authentication);
    }

    public UserModel validateThirdPartyUser(String authCode, String accessToken) throws Exception {
        Optional<UserModel> userModelByProviderIdWrapper = Optional.empty();
        Optional<UserModel> userModelByEmailWrapper = Optional.empty();
        GoogleUserInfoDTO googleUserInfoDTO = null;
        FacebookUserInfoDTO facebookUserInfoDTO = null;

        if (authCode != null) {
            GoogleTokenResponse tokenResponse = googleService.getGoogleToken(authCode);

            googleUserInfoDTO = googleService.getGoogleInfo(tokenResponse.getAccessToken())
                    .orElseThrow(() -> new BadCredentialsException("Google user info not found"));

            userModelByProviderIdWrapper = userRepository.findByProviderId(googleUserInfoDTO.getId());
            userModelByEmailWrapper = userRepository.findByEmail(googleUserInfoDTO.getEmail());
        } else if (accessToken != null) {
            facebookUserInfoDTO = facebookService.getFacebookUserInfo(accessToken)
                    .orElseThrow(() -> new BadCredentialsException("Facebook user info not found"));

            userModelByProviderIdWrapper = userRepository.findByProviderId(facebookUserInfoDTO.getId());
        }

        UserModel returnUserModel = null;

        if (userModelByProviderIdWrapper.isPresent()) {
            returnUserModel = userModelByProviderIdWrapper.get();
        } else if (userModelByEmailWrapper.isPresent()) {
            throw new BadRequestException("Email %s is already in use".formatted(userModelByEmailWrapper.get().getEmail()));
        } else {
            UserModel userModel = null;
            if (authCode != null) userModel = new UserModel(googleUserInfoDTO.getEmail(), googleUserInfoDTO.getName(), "USER", AuthProvider.GOOGLE, googleUserInfoDTO.getId(), googleUserInfoDTO.getPicture(), true);
            else if (accessToken != null) userModel = new UserModel(facebookUserInfoDTO.getEmail(), facebookUserInfoDTO.getName(), "USER", AuthProvider.FACEBOOK, facebookUserInfoDTO.getId(), facebookUserInfoDTO.getPicture().getPictureData().getUrl(), true);

            assert userModel != null;
            returnUserModel = userRepository.save(userModel);
        }

        return returnUserModel;
    }

    public String authenticateWithThirdParty(String authCode, String accessToken) throws Exception {
        UserModel returnUserModel = validateThirdPartyUser(authCode, accessToken);

        GuessItUserDetails principal = new GuessItUserDetails(
                returnUserModel.getId(),
                returnUserModel.getEmail(),
                "",
                List.of(new SimpleGrantedAuthority(returnUserModel.getRole()))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );

        return jwtService.generateToken(authentication);
    }
}

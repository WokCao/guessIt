package com.FoZ.guessIt.Controllers;

import com.FoZ.guessIt.DTOs.CodeVerifyDTO;
import com.FoZ.guessIt.DTOs.CreateEmailAccountDTO;
import com.FoZ.guessIt.Enumerations.AuthProvider;
import com.FoZ.guessIt.Models.UserModel;
import com.FoZ.guessIt.Respositories.UserRepository;
import com.FoZ.guessIt.Services.MailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {
    @Autowired
    private MailService mailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final Map<String, String> map = new HashMap<>();

    @PostMapping("/email-login")
    public String emailLogin(@RequestBody String email) {
        return "email login";
    }

    @PostMapping("/email-register")
    public ResponseEntity<?> emailRegister(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email has to be provided");
        }

        String trimmedEmail = email.trim();
        if (trimmedEmail.isEmpty()) {
            return ResponseEntity.badRequest().body("Email has to be provided");
        }

        if (userRepository.findByEmail(trimmedEmail).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        try {
            String verificationCode = String.valueOf(new Random().nextInt(900000) + 100000);
            map.put(trimmedEmail, verificationCode);
            mailService.sendVerificationCode(trimmedEmail, verificationCode);
            return ResponseEntity.ok("Verification code sent to " + trimmedEmail);
        } catch (Exception e) {
            // e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to send verification code");
        }
    }

    @PostMapping("/code-verify")
    public ResponseEntity<?> codeVerify(@RequestBody @Valid CodeVerifyDTO codeVerifyDTO) {
        if (codeVerifyDTO.getVerificationCode().isEmpty()) {
            return ResponseEntity.badRequest().body("Code has to be provided");
        }

        String trimmedCode = codeVerifyDTO.getVerificationCode().trim();
        if (trimmedCode.isEmpty()) {
            return ResponseEntity.badRequest().body("Code has to be provided");
        }

        if (map.get(codeVerifyDTO.getEmail()) != null && map.get(codeVerifyDTO.getEmail()).equals(trimmedCode)) {
            map.remove(codeVerifyDTO.getEmail());

            UserModel userModel = new UserModel();
            userModel.setEmail(codeVerifyDTO.getEmail());
            userModel.setRole("USER");
            userModel.setProvider(AuthProvider.LOCAL);
            userModel.setProviderId(null);
            userModel.setImageUrl(null);
            userModel.setAccountVerified(false);
            userRepository.save(userModel);
            return ResponseEntity.ok("Verification successful");
        } else {
            return ResponseEntity.badRequest().body("Invalid code");
        }
    }

    @PostMapping("/create-email-account")
    public ResponseEntity<?> createEmailAccount(@RequestBody @Valid CreateEmailAccountDTO createEmailAccountDTO) {
        String password = createEmailAccountDTO.getPassword();
        String confirmPassword = createEmailAccountDTO.getConfirmPassword();
        if (password.equals(confirmPassword)) {
            try {
                UserModel userModel = userRepository.findByEmail(createEmailAccountDTO.getEmail()).orElse(null);
                if (userModel == null) {
                    return ResponseEntity.badRequest().body("Failed to create account");
                }
                String hashedPassword = passwordEncoder.encode(password);
                userModel.setPassword(hashedPassword);
                userModel.setFullName(createEmailAccountDTO.getFullName());
                userModel.setAccountVerified(true);
                UserModel userModel1 = userRepository.save(userModel);

                if (userModel1.getId() > 0) {
                    return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully");
                } else {
                    return ResponseEntity.badRequest().body("Failed to create account");
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Failed to hash password");
            }
        } else {
            return ResponseEntity.badRequest().body("Password does not match");
        }
    }
}

package com.FoZ.guessIt.Controllers;

import com.FoZ.guessIt.DTOs.EmailLoginDTO;
import com.FoZ.guessIt.Models.UserModel;
import com.FoZ.guessIt.Respositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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

}

package com.FoZ.guessIt.Controllers;

import com.FoZ.guessIt.DTOs.CodeVerifyDTO;
import com.FoZ.guessIt.DTOs.CreateEmailAccountDTO;
import com.FoZ.guessIt.Services.RegisterService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/registration")
public class RegisterController {
    @Autowired
    private RegisterService registerService;
    private final Map<String, String> map = new HashMap<>();

    @PostMapping("/email-register")
    public ResponseEntity<?> emailRegister(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        try {
            String verificationCode = registerService.handleEmailRegister(email);

            assert email != null;
            String trimmedEmail = email.trim();

            /* Temporarily store the verification code in a map */
            map.put(trimmedEmail, verificationCode);

            return ResponseEntity.ok("Verification code sent to " + trimmedEmail);
        } catch (BadRequestException | RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/code-verify")
    public ResponseEntity<?> codeVerify(@Valid @RequestBody CodeVerifyDTO codeVerifyDTO) {
        if (codeVerifyDTO.getVerificationCode() == null) {
            return ResponseEntity.badRequest().body("Code has to be provided");
        }

        String trimmedCode = codeVerifyDTO.getVerificationCode().trim();
        if (trimmedCode.isEmpty()) {
            return ResponseEntity.badRequest().body("Code has to be provided");
        }

        if (map.get(codeVerifyDTO.getEmail()) != null && map.get(codeVerifyDTO.getEmail()).equals(trimmedCode)) {
            map.remove(codeVerifyDTO.getEmail());

            try {
                registerService.handleCodeVerify(codeVerifyDTO.getEmail());
                return ResponseEntity.ok("Verification successful");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid code");
        }
    }

    @PostMapping("/create-email-account")
    public ResponseEntity<?> createEmailAccount(@Valid@RequestBody CreateEmailAccountDTO createEmailAccountDTO) {
        try {
            boolean isAccountCreated = registerService.handleCreateAccount(createEmailAccountDTO);
            if (isAccountCreated) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to create account");
            }
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

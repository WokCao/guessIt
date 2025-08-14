package com.FoZ.guessIt.Services;

import com.FoZ.guessIt.DTOs.CreateEmailAccountDTO;
import com.FoZ.guessIt.Enumerations.AuthProvider;
import com.FoZ.guessIt.Models.UserModel;
import com.FoZ.guessIt.Repositories.UserRepository;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RegisterService {
    @Autowired
    private MailService mailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String handleEmailRegister(String email) throws BadRequestException {
        if (email == null) {
            throw new BadRequestException("Email has to be provided");
        }

        String trimmedEmail = email.trim();
        if (trimmedEmail.isEmpty()) {
            throw new BadRequestException("Email has to be provided");
        }

        if (userRepository.findByEmail(trimmedEmail).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        String verificationCode = String.valueOf(new Random().nextInt(900000) + 100000);
        mailService.sendVerificationCode(trimmedEmail, verificationCode);

        return verificationCode;
    }

    public void handleCodeVerify(String email) throws Exception {
        UserModel userModel = new UserModel();
        userModel.setEmail(email);
        userModel.setRole("USER");
        userModel.setProvider(AuthProvider.LOCAL);
        userModel.setProviderId(null);
        userModel.setImageUrl(null);
        userModel.setAccountVerified(false);
        userRepository.save(userModel);
    }

    public boolean handleCreateAccount(CreateEmailAccountDTO createEmailAccountDTO) throws Exception {
        String password = createEmailAccountDTO.getPassword();
        String confirmPassword = createEmailAccountDTO.getConfirmPassword();

        if (password.equals(confirmPassword)) {
            try {
                UserModel userModel = userRepository.findByEmail(createEmailAccountDTO.getEmail()).orElse(null);
                if (userModel == null) {
                    throw new BadRequestException("Failed to create account");
                }
                String hashedPassword = passwordEncoder.encode(password);
                userModel.setPassword(hashedPassword);
                userModel.setFullName(createEmailAccountDTO.getFullName());
                userModel.setAccountVerified(true);
                UserModel returnedUser = userRepository.save(userModel);

                if (returnedUser.getId() > 0) {
                    return true;
                } else {
                    throw new InternalException("Failed to create account");
                }
            } catch (Exception e) {
                if (e instanceof IllegalArgumentException) {
                    throw new IllegalArgumentException("Failed to hash password");
                } else if (e instanceof InternalException || e instanceof BadRequestException || e instanceof NullPointerException) {
                    throw e;
                }
            }
        }

        throw new BadRequestException("Password does not match");
    }
}

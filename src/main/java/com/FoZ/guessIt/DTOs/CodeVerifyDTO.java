package com.FoZ.guessIt.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CodeVerifyDTO {
    @Email
    @NotBlank
    String email;
    @Size(min = 6, max = 6)
    @NotBlank
    String verificationCode;

}

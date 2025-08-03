package com.FoZ.guessIt.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateEmailAccountDTO {
    @Email
    @NotBlank
    private String email;
    @Size(min = 2)
    @NotBlank
    private String fullName;
    @Size(min = 6, max = 30)
    @NotBlank
    private String password;
    @Size(min = 6, max = 30)
    @NotBlank
    private String confirmPassword;
}

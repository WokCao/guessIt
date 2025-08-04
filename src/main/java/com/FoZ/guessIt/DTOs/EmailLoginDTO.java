package com.FoZ.guessIt.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailLoginDTO {
    @Email
    @NotBlank
    String email;
    @Size(min = 6, max = 30)
    @NotBlank
    String password;
}

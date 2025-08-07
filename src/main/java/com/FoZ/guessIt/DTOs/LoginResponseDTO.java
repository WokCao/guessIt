package com.FoZ.guessIt.DTOs;

import com.FoZ.guessIt.Models.UserModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private UserModel userModel;
}

package com.FoZ.guessIt.DTOs;

import com.FoZ.guessIt.Models.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String fullName;
    private String imageUrl;

    public UserDTO(UserModel user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.fullName = user.getFullName();
        this.imageUrl = user.getImageUrl();
    }
}
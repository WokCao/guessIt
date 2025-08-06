package com.FoZ.guessIt.DTOs;

import lombok.Data;

@Data
public class GoogleUserInfoDTO {
    String id;
    String email;
    boolean verified_email;
    String name;
    String given_name;
    String family_name;
    String picture;
}

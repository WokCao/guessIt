package com.FoZ.guessIt.Controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {
    @PostMapping("/email-login")
    public String emailLogin(@RequestBody String email) {
        return "email login";
    }

}

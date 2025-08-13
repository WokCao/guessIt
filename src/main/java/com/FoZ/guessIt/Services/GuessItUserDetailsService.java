package com.FoZ.guessIt.Services;

import com.FoZ.guessIt.Enumerations.AuthProvider;
import com.FoZ.guessIt.Models.UserModel;
import com.FoZ.guessIt.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuessItUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    /**
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
        String password = user.getPassword();
        if (user.getProvider().equals(AuthProvider.GOOGLE) || user.getProvider().equals(AuthProvider.FACEBOOK)) {
            password = "";
        }
        return new User(user.getEmail(), password, authorities);
    }
}

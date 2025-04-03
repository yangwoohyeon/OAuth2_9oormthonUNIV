package com.example.OAuth2_9oormthonUNIV.domain.user.service;


import com.example.OAuth2_9oormthonUNIV.domain.user.Entity.User;
import com.example.OAuth2_9oormthonUNIV.domain.user.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registration(String username, String password, String email, String id){
        User user = new User();
        user.setUserId(id);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setName(username);
        return userRepository.save(user);
    }
}

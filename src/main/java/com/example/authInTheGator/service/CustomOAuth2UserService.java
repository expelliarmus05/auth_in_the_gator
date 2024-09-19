package com.example.authInTheGator.service;

import com.example.authInTheGator.entity.User;
import com.example.authInTheGator.enums.AuthProvider;
import com.example.authInTheGator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    @Autowired
    private UserRepository userRepository;

    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate to the default service to load the user's details from the OAuth2 provider
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

        // Extract email from OAuth2 provider (e.g., Google)
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        // Check if the user already exists in the database, otherwise register them
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setProvider(AuthProvider.GOOGLE);  // Assuming Google as the provider
            newUser.setUsername(oAuth2User.getAttribute("name"));  // Assuming name from provider
            return userRepository.save(newUser);
        });

        // Return the OAuth2User with user roles and attributes
        return new DefaultOAuth2User(
                Collections.singleton(() -> "ROLE_USER"),  // Default role
                oAuth2User.getAttributes(),
                "email"  // Map the user's email as the key
        );
    }
}

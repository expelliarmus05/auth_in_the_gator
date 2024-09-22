package com.example.authInTheGator.service;

import com.example.authInTheGator.entity.User;
import com.example.authInTheGator.entity.enums.AuthProvider;
import com.example.authInTheGator.entity.enums.Role;
import com.example.authInTheGator.repository.AuthUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>, UserDetailsService {
    private final AuthUserRepository authUserRepository;

    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    public CustomOAuth2UserService(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate to the default service to load the user's details from the OAuth2 provider
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

        // Extract email from OAuth2 provider (e.g., Google)
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }
        User user = new User();
        // Check if the user already exists in the database, otherwise register them
        if(!authUserRepository.existsByEmail(email)) {
            user.setFirstName(name.split(" ")[0]);
            user.setLastName(name.split(" ")[name.split(" ").length - 1]);
            user.setEmail(email);
            user.setRoles(Collections.singleton(Role.USER));
            user.setProvider(AuthProvider.GOOGLE);  // Assuming Google as the provider
            user.setUsername(oAuth2User.getAttribute("name"));  // Assuming name from provider
            authUserRepository.save(user);
        }else{
            user = authUserRepository.findByEmail(email);
        }

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());

        // Return the OAuth2User with user roles and attributes
        return new DefaultOAuth2User(
                authorities,
                oAuth2User.getAttributes(),
                "email"  // Map the user's email as the key
        );
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException, EntityNotFoundException {
        User user = authUserRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->new EntityNotFoundException("No User Found"));

        if (user == null) {
            throw new UsernameNotFoundException("User " + usernameOrEmail + "is not found");
        }
        String[] roles = user.getRoles()
                .stream().map(Enum::name).toArray(String[]::new);

        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(roles)
                .build();
    }
}

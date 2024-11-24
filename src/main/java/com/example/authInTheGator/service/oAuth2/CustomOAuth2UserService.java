package com.example.authInTheGator.service.oAuth2;

import com.example.authInTheGator.entity.AuthUser;
import com.example.authInTheGator.repository.AuthUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final Map<String, OAuth2UserHandler> oAuth2UserHandlers;
    private final AuthUserRepository authUserRepository;

    @Autowired
    public CustomOAuth2UserService(@Qualifier("google") OAuth2UserHandler googleUserService,
                                   @Qualifier("github") OAuth2UserHandler githubUserService, AuthUserRepository authUserRepository) {
        this.oAuth2UserHandlers = Map.of(
                "google", googleUserService,
                "github", githubUserService
        );
        this.authUserRepository = authUserRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Delegate to the default implementation to fetch user info
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Determine the provider (e.g., google, github)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // Fetch the corresponding handler
        OAuth2UserHandler userHandler = oAuth2UserHandlers.get(registrationId.toLowerCase());

        if (userHandler == null) {
            throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId);
        }

        // Handle the user
        userHandler.handleUser(oAuth2User);
        AuthUser user = authUserRepository.findByEmail(oAuth2User.getAttribute("email"));
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
}

package com.example.authInTheGator.service.oAuth2;

import com.example.authInTheGator.entity.AuthUser;
import com.example.authInTheGator.entity.enums.AuthProvider;
import com.example.authInTheGator.entity.enums.Role;
import com.example.authInTheGator.service.auth.AuthUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component("google")
public class GoogleUserService implements OAuth2UserHandler {
    Logger logger= LoggerFactory.getLogger(GoogleUserService.class);

    private final AuthUserService authUserService;

    public GoogleUserService(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @Override
    public void handleUser(OAuth2User oAuth2User) throws OAuth2AuthenticationException {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String sub = oAuth2User.getAttribute("sub");

        AuthUser authUser = authUserService.userExistWithEmail(email)?authUserService.findByEmail(email):new AuthUser(new HashSet<>(List.of(Role.USER)),email,email,name.split(" ")[0], name.split(" ")[1]);

        authUser.setProvider(AuthProvider.GOOGLE);
        authUser.setOAuthId(sub);

        authUserService.saveOrUpdateSocialAccount(authUser);
    }
}

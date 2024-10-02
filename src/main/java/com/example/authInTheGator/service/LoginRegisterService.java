package com.example.authInTheGator.service;

import com.example.authInTheGator.entity.AuthUser;
import com.example.authInTheGator.repository.AuthUserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginRegisterService implements UserDetailsService {
    private final AuthUserRepository authUserRepository;
    public LoginRegisterService( AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException, EntityNotFoundException {
        AuthUser authUser = authUserRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->new EntityNotFoundException("No User Found"));

        if (authUser == null) {
            throw new UsernameNotFoundException("User " + usernameOrEmail + "is not found");
        }
        String[] roles = authUser.getRoles()
                .stream().map(Enum::name).toArray(String[]::new);

        return User.withUsername(authUser.getUsername())
                .password(authUser.getPassword())
                .roles(roles)
                .build();
    }
}

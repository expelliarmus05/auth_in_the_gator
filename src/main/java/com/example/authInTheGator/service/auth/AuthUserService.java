package com.example.authInTheGator.service.auth;

import com.example.authInTheGator.entity.AuthUser;
import com.example.authInTheGator.entity.enums.AuthProvider;
import com.example.authInTheGator.entity.enums.Role;
import com.example.authInTheGator.entity.enums.VerificationType;
import com.example.authInTheGator.repository.AuthUserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Set;

@Service
public class AuthUserService {
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;

    public AuthUserService(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder, VerificationService verificationService) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationService = verificationService;
    }


    public AuthUser findById(Long id) {
        return authUserRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User not found!"));
    }

    public boolean userExistWithEmail(String email) {
        return authUserRepository.existsByEmail(email);
    }

    @Transactional
    public void saveUser(AuthUser authUser) {
        authUser.setPassword(passwordEncoder.encode(authUser.getPassword()));
        if (authUser.getRoles() == null) {
            authUser.setRoles(Set.of(Role.USER));
        }
        authUser.setProvider(AuthProvider.LOCAL);
        authUserRepository.save(authUser);
    }
    @Transactional
    public AuthUser findByEmail(String email) {
        return authUserRepository.findByEmail(email);
    }
    @Transactional
    public void saveOrUpdateSocialAccount(AuthUser account) {
        authUserRepository.save(account);
    }

    @Transactional
    public String register(AuthUser authUser, BindingResult result) {
        if (result.hasErrors()) return "auth/register";

        if (userExistWithEmail(authUser.getEmail())) {
            result.rejectValue("email", "duplicate.email",
                    "There is already an User registered with the same email");
            return "auth/register";
        }

        this.saveUser(authUser);
        verificationService.sendVerificationEmail(authUser.getEmail(), VerificationType.LINK);


        return "redirect:/auth/login";
    }
}

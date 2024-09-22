package com.example.authInTheGator.service;

import com.example.authInTheGator.entity.User;
import com.example.authInTheGator.entity.enums.AuthProvider;
import com.example.authInTheGator.entity.enums.Role;
import com.example.authInTheGator.repository.AuthUserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Optional;
import java.util.Set;

@Service
public class AuthUserService {
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthUserService(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User findById(Long id) {
        return authUserRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User not found!"));
    }

    public boolean UserExistWithEmail(String email) {
        return authUserRepository.existsByEmail(email);
    }

    @Transactional
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRoles() == null) {
            user.setRoles(Set.of(Role.USER));
        }
        user.setProvider(AuthProvider.SELF);
        authUserRepository.save(user);
    }

    @Transactional
    public String register(User User, BindingResult result) {
        if (result.hasErrors()) return "auth/register";

        if (UserExistWithEmail(User.getEmail())) {
            result.rejectValue("email", "duplicate.email",
                    "There is already an User registered with the same email");
            return "auth/register";
        }

        saveUser(User);

        return "redirect:/auth/login";
    }
}

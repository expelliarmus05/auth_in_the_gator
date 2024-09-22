package com.example.authInTheGator.service;

import com.example.authInTheGator.entity.User;
import com.example.authInTheGator.entity.enums.Role;
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

    public AuthUserService(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User findById(Long id) {
        return authUserRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User not found!"));
    }

    public User findByEmail(String email) {
        return authUserRepository.findByEmail(email).orElseGet(
                User::new
        );
    }

    public boolean UserExistWithEmail(String email) {
        return findByEmail(email).getId()==null;
    }

    @Transactional
    public void saveUser(User User) {
        User.setPassword(passwordEncoder.encode(User.getPassword()));
        if (User.getRoles() == null) {
            User.setRoles(Set.of(Role.USER));
        }
        authUserRepository.save(User);
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

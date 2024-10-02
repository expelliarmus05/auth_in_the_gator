package com.example.authInTheGator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class UsersController {
    @GetMapping("/users")
    public String users(Model model, Principal principal) {
        return "users";
    }
}

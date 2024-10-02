package com.example.authInTheGator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirect() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}

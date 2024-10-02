package com.example.authInTheGator.controller.auth;

import com.example.authInTheGator.entity.AuthUser;
import com.example.authInTheGator.service.AuthUserService;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class RegistrationController {
    private final AuthUserService authUserService;

    public RegistrationController(AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @ModelAttribute
    public AuthUser addUserToModel() {return new AuthUser();}

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new AuthUser());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute AuthUser authUser, BindingResult result) {

        return authUserService.register(authUser, result);
    }

}

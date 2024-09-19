package com.example.authInTheGator.configuration;

import com.example.authInTheGator.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http


                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/auth/register", "/auth/login", "/oauth2/**", "/css/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/auth/login")
                        .permitAll()
                        .defaultSuccessUrl("/home")
                )
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/auth/login")
                        .userInfoEndpoint((userInfo) -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("/home")
                )
                .logout((logout) -> logout.permitAll())
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/oauth2/**"))
        ;


        return http.build();
    }
}

package com.example.authInTheGator.controller.auth;

import com.example.authInTheGator.entity.AuthUser;
import com.example.authInTheGator.entity.VerificationToken;
import com.example.authInTheGator.entity.enums.VerificationType;
import com.example.authInTheGator.repository.AuthUserRepository;
import com.example.authInTheGator.repository.VerificationTokenRepository;
import com.example.authInTheGator.service.auth.VerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class VerificationController {
    private final VerificationService verificationService;
    private final VerificationTokenRepository tokenRepository;
    private final AuthUserRepository userRepository;

    public VerificationController(VerificationService verificationService, VerificationTokenRepository tokenRepository, AuthUserRepository userRepository) {
        this.verificationService = verificationService;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/verify/otp")
    public ResponseEntity<?> verifyOTP(@RequestParam String email, @RequestParam String otp) {
        try {
            if (verificationService.verifyToken(otp)) {
                if (userRepository.existsByEmail(email)) {
                    AuthUser user = userRepository.findByEmail(email);
                    user.setVerified(true);
                    userRepository.save(user);
                    return ResponseEntity.ok("Email verified successfully");
                }else {
                    throw new RuntimeException("User Not Found");
                }
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.badRequest().body("Invalid OTP");
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            if (verificationService.verifyToken(token)) {
                VerificationToken verificationToken = tokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("Token not found"));

                if (userRepository.existsByEmail(verificationToken.getEmail())) {
                    AuthUser user = userRepository.findByEmail(verificationToken.getEmail());
                    user.setVerified(true);
                    userRepository.save(user);
                    return ResponseEntity.ok("Email verified successfully");
                }else {
                    throw new RuntimeException("User Not Found");
                }
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.badRequest().body("Invalid verification link");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestParam String email,
                                                @RequestParam VerificationType type) {

        if (userRepository.existsByEmail(email)) {
            AuthUser user = userRepository.findByEmail(email);
            if (user.getVerified()) {
                return ResponseEntity.badRequest().body("Email is already verified");
            }
        }else {
            throw new RuntimeException("User Not Found");
        }

        verificationService.sendVerificationEmail(email, type);
        return ResponseEntity.ok("Verification email sent successfully");
    }

}

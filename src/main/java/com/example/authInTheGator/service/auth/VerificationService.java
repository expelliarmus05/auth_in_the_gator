package com.example.authInTheGator.service.auth;

import com.example.authInTheGator.entity.VerificationToken;
import com.example.authInTheGator.entity.data.EmailMessage;
import com.example.authInTheGator.entity.enums.VerificationType;
import com.example.authInTheGator.repository.VerificationTokenRepository;
import com.example.authInTheGator.service.email.EmailProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class VerificationService {


    private final VerificationTokenRepository tokenRepository;
    private final EmailProducer emailProducer;

    public VerificationService(VerificationTokenRepository tokenRepository, EmailProducer emailProducer) {
        this.tokenRepository = tokenRepository;
        this.emailProducer = emailProducer;
    }

    @Value("${app.verification.base-url}")
    private String baseUrl;

    public String generateOTP() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public String generateVerificationLink(String token) {
        return baseUrl + "/verify?token=" + token;
    }

    public String createVerificationToken(String email, VerificationType type) {
        // Remove any existing tokens for this email and type
        tokenRepository.findByEmailAndType(email, type)
                .ifPresent(token -> tokenRepository.delete(token));

        String token = (type == VerificationType.OTP) ?
                generateOTP() : UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken(email, token, type);
        tokenRepository.save(verificationToken);

        return token;
    }

    public void sendVerificationEmail(String email, VerificationType type) {
        String token = createVerificationToken(email, type);
        String subject = "Email Verification";
        String body;

        if (type == VerificationType.OTP) {
            body = String.format(
                    "Your verification code is: %s\nThis code will expire in 24 hours.",
                    token
            );
        } else {
            String verificationLink = generateVerificationLink(token);
            body = String.format(
                    "Please click the following link to verify your email:\n%s\nThis link will expire in 24 hours.",
                    verificationLink
            );
        }

        EmailMessage emailMessage = new EmailMessage(email, subject, body);
        emailProducer.sendEmailMessage(emailMessage);
    }

    public boolean verifyToken(String token) {
        Optional<VerificationToken> optionalToken = tokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            throw new RuntimeException("Invalid verification token");
        }

        VerificationToken verificationToken = optionalToken.get();

        if (verificationToken.isUsed()) {
            throw new RuntimeException("Token has already been used");
        }

        if (verificationToken.isExpired()) {
            throw new RuntimeException("Token has expired");
        }

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        return true;
    }

}

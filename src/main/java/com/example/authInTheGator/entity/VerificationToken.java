package com.example.authInTheGator.entity;

import com.example.authInTheGator.entity.enums.VerificationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private String email;
    private LocalDateTime expiryDate;
    private boolean used;

    @Enumerated(EnumType.STRING)
    private VerificationType type;

    public VerificationToken(String email, String token, VerificationType type) {
        this.email = email;
        this.token = token;
        this.type = type;
        this.expiryDate = LocalDateTime.now().plusHours(24); // Token valid for 24 hours
        this.used = false;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}

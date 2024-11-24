package com.example.authInTheGator.repository;

import com.example.authInTheGator.entity.VerificationToken;
import com.example.authInTheGator.entity.enums.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByEmailAndType(String email, VerificationType type);
}

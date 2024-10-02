package com.example.authInTheGator.repository;

import com.example.authInTheGator.entity.AuthUser;
import com.example.authInTheGator.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    AuthUser findByEmail(String email);
    Optional<AuthUser> findByUsernameOrEmail(String username, String email);
    Optional<AuthUser> findByUsername(String username);
    Boolean existsByEmail(String email);

    @Query("SELECT a FROM AuthUser a JOIN a.roles r WHERE r = :role")
    List<AuthUser> findByRole(@Param("role") Role role);
}

package com.example.authInTheGator.repository;

import com.example.authInTheGator.entity.User;
import com.example.authInTheGator.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByUsername(String username);
    Boolean existsByEmail(String email);

    @Query("SELECT a FROM User a JOIN a.roles r WHERE r = :role")
    List<User> findByRole(@Param("role") Role role);
}

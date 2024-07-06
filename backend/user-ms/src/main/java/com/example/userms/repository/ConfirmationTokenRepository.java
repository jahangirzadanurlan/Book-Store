package com.example.userms.repository;

import com.example.userms.model.entity.ConfirmationToken;
import com.example.userms.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,Long> {
    Optional<ConfirmationToken> findConfirmationTokenByToken(String token);
    Optional<ConfirmationToken> findConfirmationTokenByUser(User user);
}

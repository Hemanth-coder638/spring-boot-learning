package com.example.demo4.SecurityApp.repositories;

import com.example.demo4.SecurityApp.entities.Session;
import com.example.demo4.SecurityApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByUser(User user);

    Optional<Session> findByRefreshToken(String refreshToken);
    Optional<Session> deleteSessionByRefreshToken(String refreshToken);
}

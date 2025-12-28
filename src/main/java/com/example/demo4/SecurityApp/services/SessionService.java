package com.example.demo4.SecurityApp.services;

import com.example.demo4.SecurityApp.entities.Session;
import com.example.demo4.SecurityApp.entities.User;
import com.example.demo4.SecurityApp.repositories.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final JwtService jwtService;


    public void generateNewSession(User user, String refreshToken) {
        int SESSION_LIMIT = user.getSESSION_COUNT();
        List<Session> userSessions = sessionRepository.findByUser(user);
        if (userSessions.size() == SESSION_LIMIT) {
            userSessions.sort(Comparator.comparing(Session::getLastUsedAt));

            Session leastRecentlyUsedSession = userSessions.getFirst();
            sessionRepository.delete(leastRecentlyUsedSession);
        }

        Session newSession = Session.builder()
                .user(user)
                .refreshToken(refreshToken)
                .build();
        sessionRepository.save(newSession);
    }

    public void validateSession(String refreshToken) {
        sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new SessionAuthenticationException("Session not found for refreshToken: "+refreshToken));
    }

    public void deleteSession(String refreshToken) {
        log.info("Attempting delete for refreshToken = {}", refreshToken);

        Optional<Session> session =
                sessionRepository.findByRefreshToken(refreshToken);
        log.info("Session="+session);
        log.info("Session found = {}", session.isPresent());

        session.orElseThrow(() ->
                new SessionAuthenticationException(
                        "Session not found for refreshToken: " + refreshToken
                )
        );

        sessionRepository.delete(session.get());
    }


}

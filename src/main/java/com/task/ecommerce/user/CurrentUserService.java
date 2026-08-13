package com.task.ecommerce.user;

import com.task.ecommerce.entity.User;
import com.task.ecommerce.exception.BadRequestException;
import com.task.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser(Jwt jwt) {

        String keycloakId = jwt.getSubject();

        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() ->
                        new BadRequestException("User not found"));
    }

    @Transactional
    public User linkKeycloakUser(Jwt jwt) {

        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BadRequestException("User not found"));

        user.setKeycloakId(keycloakId);

        return userRepository.save(user);
    }
}
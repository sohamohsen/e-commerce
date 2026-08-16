package com.task.ecommerce.auth;

import com.task.ecommerce.Keycloak.KeycloakLoginService;
import com.task.ecommerce.Keycloak.KeycloakTokenResponse;
import com.task.ecommerce.auth.dto.AdminLoginRequest;
import com.task.ecommerce.exception.*;
import com.task.ecommerce.service.KeycloakAdminService;
import org.springframework.security.core.AuthenticationException;
import com.task.ecommerce.auth.dto.LoginRequest;
import com.task.ecommerce.auth.dto.LoginResponse;
import com.task.ecommerce.auth.dto.RegistrationRequest;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.entity.enums.Role;
import com.task.ecommerce.repository.UserRepository;
import com.task.ecommerce.security.JwtUtil;
import com.task.ecommerce.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
//    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
//    private final JwtUtil jwtUtil;
    private final RateLimiterService rateLimiterService;
    private final KeycloakAdminService keycloakAdminService;
    private final KeycloakLoginService keycloakLoginService;

    private static final int TEMP_LOCK_ATTEMPTS = 5;
    private static final int WINDOW_MIN = 1;
    private static final int PERMANENT_LOCK_ATTEMPTS = 20;
    private static final int TEMP_LOCK_MINUTES = 5;

//    @Transactional
//    public LoginResponse adminLogin(AdminLoginRequest login, HttpServletRequest request) {
//
//        String ip = request.getRemoteAddr();
//        if (!rateLimiterService.isAllowed(ip, TEMP_LOCK_ATTEMPTS, WINDOW_MIN)) {
//            throw new TooManyRequestsException("Too many attempts. Try again later.");
//        }
//
//
//        User user = getAdminUser(login.getEmail());
//
//        validateAccountStatus(user);
//
//        authenticate(user, login.getPassword());
//
//        resetFailedAttempts(user);
//
//        String token = jwtUtil.generateToken(user);
//
//        return buildLoginResponse(user, token, user.isPasswordChanged());
//    }

    private User getAdminUser(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BadRequestException("Invalid email or password"));

        if (user.getRole() != Role.ADMIN &&
                user.getRole() != Role.SUPER_ADMIN) {

            throw new BadRequestException("Invalid email or password");
        }

        return user;
    }

    private void validateAccountStatus(User user) {

        if (user.isAccountLocked()) {
            throw new AccountLockedException(
                    "Your account has been permanently locked.",
                    null
            );
        }

        if (user.getLockedUntil() == null) {
            return;
        }

        if (user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new AccountLockedException(
                    "Too many failed login attempts.",
                    user.getLockedUntil()
            );
        }

        user.setLockedUntil(null);
        user.setFailedLoginAttempt(0);

        userRepository.save(user);
    }

//    private void authenticate(User user, String password) {
//
//        log.info("Authenticating userId={}, email={}", user.getId(), user.getEmail());
//
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            String.valueOf(user.getId()),
//                            password
//                    )
//            );
//
//            log.info("Authentication succeeded for userId={}", user.getId());
//
//        } catch (BadCredentialsException ex) {
//
//            log.warn("Authentication failed for userId={}", user.getId(), ex);
//
//            handleFailedLogin(user);
//
//            throw new BadRequestException("Invalid email or password");
//
//        } catch (LockedException ex) {
//
//            throw new BadRequestException("Your account is locked. Please try again later.");
//
//        } catch (DisabledException ex) {
//
//            throw new BadRequestException("Your account is disabled.");
//
//        } catch (AuthenticationException ex) {
//
//            log.error("Authentication error", ex);
//
//            throw new BadRequestException(ex.getMessage());
//        }
//    }

    private void handleFailedLogin(User user) {

        int attempts = user.getFailedLoginAttempt() == null
                ? 0
                : user.getFailedLoginAttempt();

        attempts++;

        user.setFailedLoginAttempt(attempts);

        if (attempts >= PERMANENT_LOCK_ATTEMPTS) {
            user.setAccountLocked(true);

        } else if (attempts % TEMP_LOCK_ATTEMPTS == 0) {
            user.setLockedUntil(
                    LocalDateTime.now().plusMinutes(TEMP_LOCK_MINUTES)
            );
        }

        userRepository.save(user);
    }

    private void resetFailedAttempts(User user) {

        user.setFailedLoginAttempt(0);
        user.setLockedUntil(null);

        userRepository.save(user);
    }


    public LoginResponse userLogin(LoginRequest login) {

        log.info("=== CUSTOMER LOGIN START ===");
        log.info("Login identifier: {}", login.getIdentifier());

        User user = getCustomerUser(login.getIdentifier());

        log.info(
                "Local user found. id={}, email={}, keycloakId={}, enabled={}, role={}",
                user.getId(),
                user.getEmail(),
                user.getKeycloakId(),
                user.isEnabled(),
                user.getRole()
        );

        validateAccountStatus(user);

        log.info("Local account status validation passed");

        try {

            log.info("Calling Keycloak login for user: {}", login.getIdentifier());

            KeycloakTokenResponse token =
                    keycloakLoginService.login(
                            login.getIdentifier(),
                            login.getPassword()
                    );

            log.info("Keycloak authentication SUCCESS for user: {}",
                    login.getIdentifier());

            log.info(
                    "Keycloak token response received. tokenType={}, expiresIn={}, refreshExpiresIn={}, hasAccessToken={}, hasRefreshToken={}",
                    token.getTokenType(),
                    token.getExpiresIn(),
                    token.getRefreshExpiresIn(),
                    token.getAccessToken() != null,
                    token.getRefreshToken() != null
            );

            resetFailedAttempts(user);

            log.info(
                    "Failed login attempts reset for user id={}",
                    user.getId()
            );

            LoginResponse response = LoginResponse.builder()
                    .accessToken(token.getAccessToken())
                    .refreshToken(token.getRefreshToken())
                    .expiresIn(token.getExpiresIn())
                    .refreshExpiresIn(token.getRefreshExpiresIn())
                    .tokenType(token.getTokenType())
                    .enable(user.isEnabled())
                    .changePassword(user.isPasswordChanged())
                    .build();

            log.info(
                    "Customer login completed successfully. userId={}",
                    user.getId()
            );

            log.info("=== CUSTOMER LOGIN END ===");

            return response;

        } catch (KeycloakAuthenticationException ex) {

            log.warn(
                    "Keycloak authentication FAILED for user: {}. reason={}",
                    login.getIdentifier(),
                    ex.getMessage()
            );

            log.info(
                    "Handling failed login for local user id={}",
                    user.getId()
            );

            handleFailedLogin(user);

            log.info(
                    "Failed login handling completed for user id={}",
                    user.getId()
            );

            throw new BadRequestException(
                    "Invalid email or password"
            );
        }
    }

    private User getCustomerUser(String identifier) {

        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .orElseThrow(() -> new BadRequestException("Invalid email or phone."));
    }

    @Transactional
    public void register(
            RegistrationRequest registration,
            HttpServletRequest request
    ) {

        checkCustomerExist(
                registration.getEmail(),
                registration.getPhone()
        );

        String keycloakId = null;

        try {

            keycloakId =
                    keycloakAdminService.createCustomer(registration);

            User user = User.builder()
                    .name(registration.getName())
                    .email(registration.getEmail())
                    .phone(registration.getPhone())
                    .keycloakId(keycloakId)
                    .role(Role.CUSTOMER)
                    .enabled(true)
                    .build();

            userRepository.save(user);

        } catch (UserAlreadyExistsException ex) {

            throw ex;

        } catch (Exception ex) {

            if (keycloakId != null) {
                try {
                    keycloakAdminService.deleteUser(keycloakId);
                } catch (Exception deleteException) {
                    log.error(
                            "Failed to rollback Keycloak user {}",
                            keycloakId,
                            deleteException
                    );
                }
            }

            throw ex;
        }
    }

    private void checkCustomerExist(String email, String phone) {

        User existing = userRepository
                .findByEmailOrPhone(email, phone)
                .orElse(null);

        if (existing != null) {
            if (existing.getEmail().equals(email)) {
                throw new BadRequestException("Email already exists.");
            }

            throw new BadRequestException("Phone already exists.");
        }
    }

    public LoginResponse refreshToken(String refreshToken) {

        KeycloakTokenResponse token =
                keycloakLoginService.refresh(refreshToken);

        return LoginResponse.builder()
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .expiresIn(token.getExpiresIn())
                .refreshExpiresIn(token.getRefreshExpiresIn())
                .tokenType(token.getTokenType())
                .build();
    }

    public void logout(String refreshToken) {

        log.info("Logging out from Keycloak");

        keycloakLoginService.logout(refreshToken);

        log.info("Logout completed successfully");
    }
}
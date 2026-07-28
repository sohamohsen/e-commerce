package com.task.ecommerce.Auth;

import com.task.ecommerce.Auth.dto.AdminLoginRequest;
import com.task.ecommerce.Auth.dto.LoginRequest;
import com.task.ecommerce.Auth.dto.LoginResponse;
import com.task.ecommerce.Auth.dto.RegistrationRequest;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.entity.enums.Role;
import com.task.ecommerce.exception.AccountLockedException;
import com.task.ecommerce.exception.BadRequestException;
import com.task.ecommerce.exception.TooManyRequestsException;
import com.task.ecommerce.repository.UserRepository;
import com.task.ecommerce.security.JwtUtil;
import com.task.ecommerce.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RateLimiterService rateLimiterService;

    private static final int TEMP_LOCK_ATTEMPTS = 5;
    private static final int WINDOW_MIN = 1;
    private static final int PERMANENT_LOCK_ATTEMPTS = 20;
    private static final int TEMP_LOCK_MINUTES = 5;

    @Transactional
    public LoginResponse adminLogin(AdminLoginRequest login, HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        if (!rateLimiterService.isAllowed(ip, TEMP_LOCK_ATTEMPTS, WINDOW_MIN)) {
            throw new TooManyRequestsException("Too many attempts. Try again later.");
        }


        User user = getAdminUser(login.getEmail());

        validateAccountStatus(user);

        authenticate(user, login.getPassword());

        resetFailedAttempts(user);

        String token = jwtUtil.generateToken(user);

        return buildLoginResponse(user, token);
    }

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

    private void authenticate(User user, String password) {

        log.info("Authenticating userId={}, email={}", user.getId(), user.getEmail());

        try {
            log.info("password={}", passwordEncoder.encode(password));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            String.valueOf(user.getId()),
                            password
                    )
            );

            log.info("Authentication succeeded for userId={}", user.getId());

        } catch (BadCredentialsException ex) {

            log.warn("Authentication failed for userId={}", user.getId());

            handleFailedLogin(user);

            throw new BadRequestException("Invalid email or password");
        }
    }

    private void handleFailedLogin(User user) {

        user.setFailedLoginAttempt(user.getFailedLoginAttempt() + 1);

        if (user.getFailedLoginAttempt() >= PERMANENT_LOCK_ATTEMPTS) {
            user.setAccountLocked(true);

        } else if (user.getFailedLoginAttempt() % TEMP_LOCK_ATTEMPTS == 0) {
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

    private LoginResponse buildLoginResponse(User user, String token) {

        return LoginResponse.builder()
                .token(token)
                .enable(user.isEnabled())
                .build();
    }

    public LoginResponse userLogin(LoginRequest login, HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        if (!rateLimiterService.isAllowed(ip, TEMP_LOCK_ATTEMPTS, WINDOW_MIN)) {
            throw new TooManyRequestsException("Too many attempts. Try again later.");
        }

        User user = getCustomerUser(login.getIdentifier());

        validateAccountStatus(user);

        authenticate(user, login.getPassword());

        resetFailedAttempts(user);

        String token = jwtUtil.generateToken(user);

        return buildLoginResponse(user, token);
    }

    private User getCustomerUser(String identifier) {

        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .orElseThrow(() -> new BadRequestException("Invalid email or phone."));
    }

    @Transactional
    public void register(RegistrationRequest registration, HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        if (!rateLimiterService.isAllowed(ip, TEMP_LOCK_ATTEMPTS, WINDOW_MIN)) {
            throw new TooManyRequestsException("Too many attempts. Try again later.");
        }

        checkCustomerExist(registration.getEmail(), registration.getPhone());

        User user = User.builder()
                .name(registration.getName())
                .email(registration.getEmail())
                .phone(registration.getPhone())
                .password(passwordEncoder.encode(registration.getPassword()))
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        userRepository.save(user);
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
}
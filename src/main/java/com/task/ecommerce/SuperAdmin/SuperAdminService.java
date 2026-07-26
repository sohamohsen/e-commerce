package com.task.ecommerce.SuperAdmin;

import com.task.ecommerce.SuperAdmin.dto.AdminAccount;
import com.task.ecommerce.SuperAdmin.dto.CreateAdminAccount;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.entity.enums.Role;
import com.task.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.task.ecommerce.exception.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class SuperAdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int PASSWORD_LENGTH = 12;
    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";

    @Transactional
    public AdminAccount createAdmin(CreateAdminAccount request, Integer superAdminId) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new BadRequestException("This mail is already exist.");
        }

        if(userRepository.existsByPhone(request.getPhone())){
            throw new BadRequestException("This phone is already exist.");
        }

        String password = generateRandomPassword();

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .enabled(false)
                .createdBy(superAdminId)
                .build();

        userRepository.save(user);

        return AdminAccount.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .password(password)
                .build();
    }

    private String generateRandomPassword() {

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    @Transactional
    public void toggleLockAccount(Integer userId, Integer superAdminId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getId().equals(superAdminId)) {
            throw new BadRequestException("You cannot lock your own account.");
        }

        user.setAccountLocked(!user.isAccountNonLocked());
        user.setFailedLoginAttempt(0);
        user.setLockedUntil(null);
        user.setUpdatedBy(superAdminId);

        userRepository.save(user);
    }
}

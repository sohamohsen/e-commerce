package com.task.ecommerce.SuperAdmin;

import com.task.ecommerce.SuperAdmin.dto.AdminAccount;
import com.task.ecommerce.SuperAdmin.dto.AdminResponse;
import com.task.ecommerce.SuperAdmin.dto.CreateAdminAccount;
import com.task.ecommerce.admin.dto.CategoryResponse;
import com.task.ecommerce.admin.dto.ProductResponse;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.entity.enums.Role;
import com.task.ecommerce.event.AdminTemporaryPasswordGeneratedEvent;
import com.task.ecommerce.repository.UserRepository;
import com.task.ecommerce.utils.PageResponse;
import lombok.RequiredArgsConstructor;
import com.task.ecommerce.exception.BadRequestException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuperAdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    private static final java.util.Set<String> ALLOWED_SORT_FIELDS =
            java.util.Set.of("name", "price", "quantity", "createdAt", "updatedAt");
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
                .enabled(true)
                .passwordChanged(false)
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

    @Transactional
    public String ChangeAdminPassword(Integer userId, Integer id) {

        User superAdmin = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));

        User admin = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("admin not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new BadRequestException("User is not an admin.");
        }


        String password = generateRandomPassword();

        admin.setPassword(passwordEncoder.encode(password));
        admin.setPasswordChanged(false);
        admin.setUpdatedBy(superAdmin.getId());

        userRepository.save(admin);

//        eventPublisher.publishEvent(
//                new AdminTemporaryPasswordGeneratedEvent(
//                        user.getId(),
//                        user.getEmail(),
//                        temporaryPassword
//                )
        return password;
    }

    public PageResponse<AdminResponse> getAllAdmins(int page, int size, String sortBy, String sortDir, String search, Boolean enabled, Boolean accountLocked, Boolean passwordChanged, LocalDateTime createdFrom, LocalDateTime createdTo) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        Page<User> admins = userRepository.findAdmins(search, enabled, accountLocked, passwordChanged, createdFrom, createdTo, pageable);

        List<AdminResponse> adminsResponse = admins.getContent()
                .stream()
                .map(admin -> AdminResponse.builder()
                        .id(admin.getId())
                        .name(admin.getName())
                        .email(admin.getEmail())
                        .phone(admin.getPhone())
                        .enabled(admin.getEnabled())
                        .accountLocked(admin.isAccountLocked())
                        .passwordChanged(admin.isPasswordChanged())
                        .createdAt(admin.getCreatedAt())
                        .build())
                .toList();

        return PageResponse.<AdminResponse>builder()
                .items(adminsResponse)
                .page(admins.getNumber())
                .size(admins.getSize())
                .totalElements(admins.getTotalElements())
                .totalPages(admins.getTotalPages())
                .first(admins.isFirst())
                .last(admins.isLast())
                .build();

    }
}

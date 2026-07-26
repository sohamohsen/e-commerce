package com.task.ecommerce.admin;

import com.task.ecommerce.admin.dto.ActivateAccount;
import com.task.ecommerce.entity.User;
import com.task.ecommerce.exception.BadRequestException;
import com.task.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public void activateAccount(ActivateAccount account, Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.isEnabled()) {
            throw new BadRequestException("Your account is already activated.");
        }

        user.setEnabled(true);
        changePassword(account.getPassword(), user);
        userRepository.save(user);
    }

    private void changePassword(String password, User user){
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}

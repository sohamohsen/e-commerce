package com.task.ecommerce.user;

import com.task.ecommerce.exception.UnauthorizedException;
import com.task.ecommerce.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TokenBlacklistService blackListService;

    public void logout(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Token ")) {
            throw new UnauthorizedException("Token is missing.");
        }

        String token = authHeader.substring("Token ".length());

        blackListService.blacklist(token);
    }
}

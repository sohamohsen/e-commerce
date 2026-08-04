package com.task.ecommerce.admin;

import com.task.ecommerce.admin.dto.AdminAvailabilityRequest;
import com.task.ecommerce.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Chat (Admin Presence)", description = "Endpoints for managing admin real-time chat availability")
public class ChatAdminController {

    private final AdminPresenceService adminPresenceService;

    @Operation(summary = "Update admin chat availability", description = "Sets whether an admin is available or unavailable for live customer support chat.")
    @PostMapping("/availability")
    public ResponseEntity<Void> updateAvailability(
            @AuthenticationPrincipal User user,
            @RequestBody AdminAvailabilityRequest request
    ) {

        if (request.isAvailable()) {
            adminPresenceService.makeAvailable(user.getId());
        } else {
            adminPresenceService.makeUnavailable(user.getId());
        }

        return ResponseEntity.ok().build();
    }

}
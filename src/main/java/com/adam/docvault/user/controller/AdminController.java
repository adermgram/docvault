package com.adam.docvault.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adam.docvault.user.entity.User;
import com.adam.docvault.user.service.AdminUserService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminUserService adminUserService;

    public AdminController(AdminUserService adminUserService){
        this.adminUserService = adminUserService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{userId}/promote")
    public ResponseEntity<Void> promoteToAdmin(
            @PathVariable UUID userId
    ) {
        adminUserService.promoteToAdmin(userId);
        return ResponseEntity.noContent().build();
    }
    

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}/demote")
    public ResponseEntity<Void> demoteToUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User adminUser
    ) {
        adminUserService.demoteToUser(userId, adminUser);

        return ResponseEntity.noContent().build();
    }

}

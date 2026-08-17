package com.adam.docvault.user.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adam.docvault.user.dto.AdminUserResponseDTO;
import com.adam.docvault.user.entity.User;
import com.adam.docvault.user.service.AdminUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@SecurityRequirement(name = "bearerAuth")
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public Page<AdminUserResponseDTO> getUsers(
         @RequestParam(required = false) String search,
         Pageable pageable) {
        return adminUserService.getUsers(search, pageable);
    }

}

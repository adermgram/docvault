package com.adam.docvault;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin")
public class AdminTestController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String adminOnly() {
        return "Only admins can see this";
    }
}

package com.adam.docvault;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.adam.docvault.user.entity.User;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This endpoint is public";
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/private")
    public String privateEndpoint(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        return "Hello " + user.getFirstName();
    }
}

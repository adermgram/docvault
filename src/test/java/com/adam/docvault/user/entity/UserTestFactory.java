package com.adam.docvault.user.entity;

import java.util.UUID;

public final class UserTestFactory {

    private UserTestFactory() {
    }

    public static User create(
            UUID id,
            Role role
    ) {
        return new User(
                id,
                "Test",
                "User",
                "test@example.com",
                "password",
                role
        );
    }
}
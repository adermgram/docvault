package com.adam.docvault.user.validation;

public enum UserSortField {
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    EMAIL("email"),
    ROLE("role"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String property;

    UserSortField(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}

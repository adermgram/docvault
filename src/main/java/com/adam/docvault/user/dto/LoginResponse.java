package com.adam.docvault.user.dto;

import com.adam.docvault.user.entity.Role;

public record LoginResponse(

    String email,
    Role role

) {}
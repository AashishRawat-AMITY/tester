package com.cybercup.cybercup_backend.dto;

import java.util.List;

public class AuthMeResponse {

    private Long userId;
    private String fullName;
    private String email;
    private List<String> roles;

    public AuthMeResponse(Long userId, String fullName,
                          String email, List<String> roles) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
}

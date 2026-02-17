package org.example.basiclogin.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String getAllUsers() {
        return "This is a protected endpoint. Only users with ADMIN role can access this.";
    }
    @GetMapping("/user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public String getUserProfile() {
        return "This is a protected endpoint. Only authenticated users can access this.";
    }
}

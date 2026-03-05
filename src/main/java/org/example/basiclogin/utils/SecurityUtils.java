package org.example.basiclogin.utils;

import org.example.basiclogin.exception.UnauthorizedException;
import org.example.basiclogin.model.Entity.AppUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static AppUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Unauthorized");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AppUser appUser) {
            return appUser;
        }

        throw new UnauthorizedException("Unauthorized");
    }

    public static Long currentUserId() {
        AppUser user = currentUser();
        if (user.getId() == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        return user.getId();
    }
}


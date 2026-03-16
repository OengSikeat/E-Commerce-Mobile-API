package org.example.basiclogin.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basiclogin.exception.BadRequestException;
import org.example.basiclogin.model.Entity.AppUser;

import org.example.basiclogin.model.Request.AppUserRequest;
import org.example.basiclogin.model.Response.AppUserResponse;
import org.example.basiclogin.repository.AppUserRepository;
import org.example.basiclogin.service.AppUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserResponse register(AppUserRequest request) {

        // Duplicate email check
        AppUser exists = appUserRepository.getUserByEmail(request.getEmail());
        if (exists != null) {
            throw new BadRequestException("Email already in use");
        }

        if (request.getPassword() == null || request.getConfirmPassword() == null ||
                !request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and confirmPassword must match");
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        AppUser appUser = appUserRepository.register(request);

        AppUser updatedUser = appUserRepository.getUserById(appUser.getId());

        return AppUserResponse.builder()
                .id(updatedUser.getId())
                .fullName(updatedUser.getFullName())
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole() == null ? null : updatedUser.getRole().name())
                .createdAt(updatedUser.getCreatedAt())
                .build();
    }

    private AppUserResponse toResponse(AppUser user) {
        if (user == null) return null;
        return AppUserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.getUserByEmail(email);
    }
}

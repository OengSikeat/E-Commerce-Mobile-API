package org.example.basiclogin.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basiclogin.exception.BadRequestException;
import org.example.basiclogin.model.Entity.AppUser;

import org.example.basiclogin.model.Request.AppUserRequest;
import org.example.basiclogin.model.Response.AppUserResponse;
import org.example.basiclogin.repository.AppUserRepository;
import org.example.basiclogin.service.AppUserService;
import org.example.basiclogin.utils.PaginatedResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        AppUser appUser = appUserRepository.register(request, getCurrentUserId());

        String role = request.getRole();
        if (role != null) {
            if (role.equals("ROLE_SUPER-ADMIN")) {
                appUserRepository.insertUserIdAndRoleId(1L, appUser.getUserId());
            }
            if (role.equals("ROLE_ADMIN")) {
                appUserRepository.insertUserIdAndRoleId(2L, appUser.getUserId());
            }
            if (role.equals("ROLE_MANAGER")) {
                appUserRepository.insertUserIdAndRoleId(3L, appUser.getUserId());
            }
            if (role.equals("ROLE_USER")) {
                appUserRepository.insertUserIdAndRoleId(4L, appUser.getUserId());
            }
        }

        AppUser updatedUser = appUserRepository.getUserById(appUser.getUserId());

        return AppUserResponse.builder()
                .id(updatedUser.getUserId())
                .fullName(updatedUser.getFullName())
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole())
                .createdBy(updatedUser.getCreatedBy())
                .build();
    }

    @Override
    public AppUserResponse update(Long userId, AppUserRequest request) {
        // Duplicate email check: allow same email for the same user
        AppUser existing = appUserRepository.getUserByEmail(request.getEmail());
        if (existing != null && !existing.getUserId().equals(userId)) {
            throw new BadRequestException("Email already in use by another user");
        }
        AppUser updated = appUserRepository.update(userId, request);
        return toResponse(updated);
    }

    @Override
    public void delete(Long userId) {
        appUserRepository.delete(userId);
    }

    @Override
    public PaginatedResponse<AppUserResponse> findAll(int page, int size) {
        int offset = Math.max(0, page) * size;
        List<AppUser> users = appUserRepository.findAllPaged(size, offset);
        long total = appUserRepository.countAll();
        List<AppUserResponse> items = users.stream().map(this::toResponse).collect(Collectors.toList());
        int totalPages = (int) Math.ceil((double) total / size);
        return new PaginatedResponse<>(items, total, page, size, totalPages);
    }

    @Override
    public PaginatedResponse<AppUserResponse> findAllByCreatorId(Long creatorId, int page, int size) {
        int offset = Math.max(0, page) * size;
        List<AppUser> users = appUserRepository.findAllByCreatorIdPaged(creatorId, size, offset);
        long total = appUserRepository.countByCreatorId(creatorId);
        List<AppUserResponse> items = users.stream().map(this::toResponse).collect(Collectors.toList());
        int totalPages = (int) Math.ceil((double) total / size);
        return new PaginatedResponse<>(items, total, page, size, totalPages);
    }

    @Override
    public AppUserResponse findById(Long userId) {
        AppUser user = appUserRepository.getUserById(userId);
        return toResponse(user);
    }

    @Override
    public Long getCurrentUserId() {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        Long userId = appUser.getUserId();
        return userId;
    }

    private AppUserResponse toResponse(AppUser user) {
        if (user == null) return null;
        return AppUserResponse.builder()
                .id(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdBy(user.getCreatedBy())
                .build();
    }



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.getUserByEmail(email);
    }
}

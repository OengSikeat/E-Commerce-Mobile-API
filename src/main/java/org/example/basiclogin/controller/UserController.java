package org.example.basiclogin.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.basiclogin.model.Request.AppUserRequest;
import org.example.basiclogin.model.Response.AppUserResponse;
import org.example.basiclogin.service.AppUserService;
import org.example.basiclogin.utils.ApiResponse;
import org.example.basiclogin.utils.BaseResponse;
import org.example.basiclogin.utils.PaginatedResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController extends BaseResponse {
    private final AppUserService appUserService;

    // Create: SUPER-ADMIN, ADMIN, MANAGER
    @PreAuthorize("hasRole('SUPER-ADMIN') or hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<ApiResponse<AppUserResponse>> createUser(@Valid @RequestBody AppUserRequest request) {
        AppUserResponse created = appUserService.register(request);
        return responseEntity(true, "User "+created.getId()+" created successfully", HttpStatus.CREATED, created);
    }

    // Update: SUPER-ADMIN, ADMIN
    @PreAuthorize("hasRole('SUPER-ADMIN') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AppUserResponse>> updateUser(@PathVariable("id") Long id, @Valid @RequestBody AppUserRequest request) {
        AppUserResponse updated = appUserService.update(id, request);
        return responseEntity(true, "User "+updated.getId()+" updated successfully", HttpStatus.OK, updated);
    }

    // Delete: SUPER-ADMIN only
    @PreAuthorize("hasRole('SUPER-ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") Long id) {
        appUserService.delete(id);
        return responseEntity(true, "User deleted successfully", HttpStatus.OK);
    }

    // Get all: everyone
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<AppUserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginatedResponse<AppUserResponse> resp = appUserService.findAll(page-1, size);
        return responseEntity(true, "All User fetched successfully", HttpStatus.OK, resp);
    }

    // Get all by current user id: ADMIN and MANAGER
    @PreAuthorize("hasRole('SUPER-ADMIN') or hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/creator")
    public ResponseEntity<ApiResponse<PaginatedResponse<AppUserResponse>>> getAllUsersByCreator(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long creatorId = appUserService.getCurrentUserId();
        PaginatedResponse<AppUserResponse> resp = appUserService.findAllByCreatorId(creatorId, page-1, size);
        return responseEntity(true, "All User from creator fetched successfully", HttpStatus.OK, resp);
    }

    // Get user by id: everyone
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppUserResponse>> getUserById(@PathVariable("id") Long id) {
        AppUserResponse resp = appUserService.findById(id);
        return responseEntity(true, "User "+id+" fetched successfully", HttpStatus.OK, resp);
    }
}

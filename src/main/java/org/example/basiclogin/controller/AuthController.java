package org.example.basiclogin.controller;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.basiclogin.jwt.JwtService;
import org.example.basiclogin.model.Request.AppUserRequest;
import org.example.basiclogin.model.Request.AuthRequest;
import org.example.basiclogin.model.Request.UpdateProfileRequest;
import org.example.basiclogin.model.Response.AuthResponse;
import org.example.basiclogin.model.Response.AppUserResponse;
import org.example.basiclogin.repository.AppUserRepository;
import org.example.basiclogin.service.AppUserService;
import org.example.basiclogin.utils.ApiResponse;
import org.example.basiclogin.utils.BaseResponse;
import org.example.basiclogin.model.Entity.AppUser;
import org.example.basiclogin.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auths")
@RequiredArgsConstructor
public class AuthController extends BaseResponse {
    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticate(@RequestBody AuthRequest request) throws Exception {
        authenticate(request.getEmail(), request.getPassword());
        final UserDetails userDetails = appUserService.loadUserByUsername(request.getEmail());
        final String token = jwtService.generateToken(userDetails);
        AuthResponse authResponse = new AuthResponse(token);
        return responseEntity(true, "Login successful", HttpStatus.OK, authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AppUserResponse>> register(@Valid @RequestBody AppUserRequest request){
        return responseEntity(true, "User registered", HttpStatus.CREATED, appUserService.register(request));
    }

    @GetMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<AppUserResponse>> profile() {
        AppUser user = SecurityUtils.currentUser();
        AppUserResponse response = AppUserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
        return responseEntity(true, "Profile fetched", HttpStatus.OK, response);
    }

    @PatchMapping("/profile")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<AppUserResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        AppUser current = SecurityUtils.currentUser();

        String newEmail = request.getEmail() != null ? request.getEmail() : current.getEmail();
        String newFullName = request.getFullName() != null ? request.getFullName() : current.getFullName();

        String encodedPassword = null;
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            encodedPassword = passwordEncoder.encode(request.getPassword());
        }

        int updatedRows = appUserRepository.updateProfile(current.getId(), newEmail, newFullName, encodedPassword);
        if (updatedRows == 0) {
            throw new org.example.basiclogin.exception.NotFoundException("User not found");
        }

        AppUser updated = appUserRepository.getUserById(current.getId());
        if (updated == null) {
            throw new org.example.basiclogin.exception.NotFoundException("User not found");
        }

        AppUserResponse response = AppUserResponse.builder()
                .id(updated.getId())
                .fullName(updated.getFullName())
                .email(updated.getEmail())
                .createdAt(updated.getCreatedAt())
                .build();

        return responseEntity(true, "Profile updated", HttpStatus.OK, response);
    }

}

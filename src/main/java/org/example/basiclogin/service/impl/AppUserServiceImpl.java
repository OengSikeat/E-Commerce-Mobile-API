package org.example.basiclogin.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.basiclogin.model.AppUser;

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

        request.setPassword(passwordEncoder.encode(request.getPassword()));

        AppUser appUser = appUserRepository.register(request);

        for (String role : request.getRoles()) {
            if (role.equals("ROLE_USER")) {
                appUserRepository.insertUserIdAndRoleId(1L, appUser.getUserId());
            }
            if (role.equals("ROLE_ADMIN")) {
                appUserRepository.insertUserIdAndRoleId(2L, appUser.getUserId());
            }
        }

        // 🔥 Fetch again to load roles properly
        AppUser updatedUser = appUserRepository.getUserById(appUser.getUserId());

        return AppUserResponse.builder()
                .id(updatedUser.getUserId())
                .fullName(updatedUser.getFullName())
                .email(updatedUser.getEmail())
                .roles(updatedUser.getRoles())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.getUserByEmail(email);
    }
}

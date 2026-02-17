package org.example.basiclogin.service;

import org.example.basiclogin.model.Request.AppUserRequest;
import org.example.basiclogin.model.Response.AppUserResponse;
import org.example.basiclogin.utils.PaginatedResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppUserService extends UserDetailsService {
    AppUserResponse register(AppUserRequest request);

    AppUserResponse update(Long userId, AppUserRequest request);

    void delete(Long userId);

    PaginatedResponse<AppUserResponse> findAll(int page, int size);

    PaginatedResponse<AppUserResponse> findAllByCreatorId(Long creatorId, int page, int size);

    AppUserResponse findById(Long userId);

    Long getCurrentUserId();

}

package org.example.basiclogin.service;

import org.example.basiclogin.model.Request.AppUserRequest;
import org.example.basiclogin.model.Response.AppUserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppUserService extends UserDetailsService {
    AppUserResponse register(AppUserRequest request);

}

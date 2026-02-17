package org.example.basiclogin.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.basiclogin.jwt.JwtService;
import org.example.basiclogin.repository.BlacklistTokenRepository;
import org.example.basiclogin.service.BlacklistTokenService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class BlacklistTokenServiceImpl implements BlacklistTokenService {
    private final BlacklistTokenRepository blacklistRepository;
    private final JwtService jwtService;

    @Override
    public void blacklistToken(String token) {
        Date expiry = jwtService.extractExpirationDate(token);
        blacklistRepository.insertToken(token, expiry);
    }

    @Override
    public boolean isBlacklisted(String token) {
        return blacklistRepository.existsByToken(token);
    }

}

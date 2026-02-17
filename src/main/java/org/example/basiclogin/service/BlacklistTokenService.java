package org.example.basiclogin.service;

public interface BlacklistTokenService {
    void blacklistToken(String token);
    boolean isBlacklisted(String token);

}

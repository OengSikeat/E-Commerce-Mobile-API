package org.example.basiclogin.service;

public interface WaitlistService {
    boolean toggle(Long productId);

    boolean set(Long productId, boolean inWaitlist);
}


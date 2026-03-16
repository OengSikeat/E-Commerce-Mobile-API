package org.example.basiclogin.service;

public interface WishlistService {
    boolean toggle(Long productId);
    boolean set(Long productId, boolean inWishlist);
}

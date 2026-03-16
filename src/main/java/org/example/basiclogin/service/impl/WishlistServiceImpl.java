package org.example.basiclogin.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.basiclogin.exception.BadRequestException;
import org.example.basiclogin.exception.NotFoundException;
import org.example.basiclogin.model.Entity.Product;
import org.example.basiclogin.repository.ProductRepository;
import org.example.basiclogin.repository.WishlistRepository;
import org.example.basiclogin.service.WishlistService;
import org.example.basiclogin.utils.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    @Override
    public boolean toggle(Long productId) {
        if (productId == null) throw new BadRequestException("Product id is required");
        Product product = productRepository.findById(productId);
        if (product == null) throw new NotFoundException("Product not found");

        Long userId = SecurityUtils.currentUserId();
        boolean exists = wishlistRepository.exists(userId, productId) > 0;
        if (exists) {
            wishlistRepository.remove(userId, productId);
            return false;
        }
        wishlistRepository.add(userId, productId);
        return true;
    }

    @Override
    public boolean set(Long productId, boolean inWishlist) {
        if (productId == null) throw new BadRequestException("Product id is required");
        Product product = productRepository.findById(productId);
        if (product == null) throw new NotFoundException("Product not found");

        Long userId = SecurityUtils.currentUserId();
        boolean exists = wishlistRepository.exists(userId, productId) > 0;

        if (inWishlist) {
            if (!exists) wishlistRepository.add(userId, productId);
            return true;
        }

        if (exists) wishlistRepository.remove(userId, productId);
        return false;
    }
}


package org.example.basiclogin.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.basiclogin.exception.BadRequestException;
import org.example.basiclogin.exception.NotFoundException;
import org.example.basiclogin.model.Entity.Product;
import org.example.basiclogin.repository.ProductRepository;
import org.example.basiclogin.repository.WaitlistRepository;
import org.example.basiclogin.service.WaitlistService;
import org.example.basiclogin.utils.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WaitlistServiceImpl implements WaitlistService {

    private final WaitlistRepository waitlistRepository;
    private final ProductRepository productRepository;

    @Override
    public boolean toggle(Long productId) {
        if (productId == null) throw new BadRequestException("Product id is required");
        Product product = productRepository.findById(productId);
        if (product == null) throw new NotFoundException("Product not found");

        Long userId = SecurityUtils.currentUserId();
        boolean exists = waitlistRepository.exists(userId, productId) > 0;
        if (exists) {
            waitlistRepository.leave(userId, productId);
            return false;
        }
        waitlistRepository.join(userId, productId);
        return true;
    }

    @Override
    public boolean set(Long productId, boolean inWaitlist) {
        if (productId == null) throw new BadRequestException("Product id is required");
        Product product = productRepository.findById(productId);
        if (product == null) throw new NotFoundException("Product not found");

        Long userId = SecurityUtils.currentUserId();
        boolean exists = waitlistRepository.exists(userId, productId) > 0;

        if (inWaitlist) {
            if (!exists) waitlistRepository.join(userId, productId);
            return true;
        }

        if (exists) waitlistRepository.leave(userId, productId);
        return false;
    }
}


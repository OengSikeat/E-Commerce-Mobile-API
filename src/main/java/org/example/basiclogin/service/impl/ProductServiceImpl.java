package org.example.basiclogin.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.basiclogin.exception.BadRequestException;
import org.example.basiclogin.exception.NotFoundException;
import org.example.basiclogin.model.Entity.Product;
import org.example.basiclogin.model.Request.DiscountRequest;
import org.example.basiclogin.model.Request.ProductRequest;
import org.example.basiclogin.model.Response.ProductResponse;
import org.example.basiclogin.repository.ProductRepository;
import org.example.basiclogin.service.ProductService;
import org.example.basiclogin.utils.CategoryUtils;
import org.example.basiclogin.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private ProductResponse toResponse(Product p) {
        if (p == null) return null;
        return ProductResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .imageUrl(p.getImageUrl())
                .sizeOptions(p.getSizeOptions())
                .category(p.getCategory())
                .discountPercentage(p.getDiscountPercentage())
                .createdBy(p.getCreatedBy())
                .onPromotion(p.getOnPromotion())
                .createdAt(p.getCreatedAt())
                .build();
    }

    @Override
    public ProductResponse create(org.example.basiclogin.model.Enum.ProductCategory category, ProductRequest request) {
        if (category == null) throw new BadRequestException("category is required");
        if (request == null) throw new BadRequestException("Request body is required");

        String normalizedCategory = CategoryUtils.normalizeOrThrow(category.name());

        Long createdBy = SecurityUtils.currentUserId();
        Product created = productRepository.create(request, createdBy, normalizedCategory);
        return toResponse(created);
    }

    @Override
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id);
        if (product == null) throw new NotFoundException("Product not found");
        return toResponse(product);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        // ensure exists (also gives better 404 than silent null)
        Product existing = productRepository.findById(id);
        if (existing == null) throw new NotFoundException("Product not found");

        // category is not updated here (it comes from the enum param on create only)
        Product updated = productRepository.update(id, request);
        return toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Product existing = productRepository.findById(id);
        if (existing == null) throw new NotFoundException("Product not found");
        productRepository.delete(id);
    }

    @Override
    public List<ProductResponse> getAll(String category,
                                       Boolean newArrivals,
                                       String sortPrice,
                                       String sortCreatedAt,
                                       Long createdBy,
                                       Boolean trending,
                                       String name) {
        if (sortPrice != null && !sortPrice.isBlank()) {
            String normalized = sortPrice.trim().toLowerCase();
            if (!normalized.equals("asc") && !normalized.equals("desc")) {
                throw new BadRequestException("sortPrice must be 'asc' or 'desc'");
            }
            sortPrice = normalized;
        }

        if (sortCreatedAt != null && !sortCreatedAt.isBlank()) {
            String normalized = sortCreatedAt.trim().toLowerCase();
            if (!normalized.equals("asc") && !normalized.equals("desc")) {
                throw new BadRequestException("sortCreatedAt must be 'asc' or 'desc'");
            }
            sortCreatedAt = normalized;
        }

        String normalizedCategory = null;
        if (category != null && !category.isBlank()) {
            normalizedCategory = CategoryUtils.normalizeOrThrow(category);
        }

        return productRepository.findAllFiltered(normalizedCategory, newArrivals, sortPrice, sortCreatedAt, createdBy, trending, name)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ProductResponse updateDiscount(Long id, DiscountRequest request) {
        if (id == null) throw new BadRequestException("Product id is required");
        if (request == null || request.getPercentage() == null) {
            throw new BadRequestException("percentage is required");
        }
        BigDecimal pct = request.getPercentage();
        if (pct.compareTo(BigDecimal.ZERO) < 0 || pct.compareTo(new BigDecimal("100")) > 0) {
            throw new BadRequestException("percentage must be between 0 and 100");
        }

        int updatedRows = productRepository.updateDiscount(id, pct);
        if (updatedRows == 0) throw new NotFoundException("Product not found");

        Product updated = productRepository.findById(id);
        if (updated == null) throw new NotFoundException("Product not found");
        return toResponse(updated);
    }
}

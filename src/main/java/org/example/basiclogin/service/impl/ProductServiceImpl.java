package org.example.basiclogin.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.basiclogin.exception.NotFoundException;
import org.example.basiclogin.model.Entity.Product;
import org.example.basiclogin.model.Request.ProductRequest;
import org.example.basiclogin.model.Response.ProductResponse;
import org.example.basiclogin.repository.ProductRepository;
import org.example.basiclogin.service.ProductService;
import org.springframework.stereotype.Service;

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
                .onPromotion(p.getOnPromotion())
                .build();
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        Product created = productRepository.create(request);
        return toResponse(created);
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
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
        Product updated = productRepository.update(id, request);
        return toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Product existing = productRepository.findById(id);
        if (existing == null) throw new NotFoundException("Product not found");
        productRepository.delete(id);
    }
}


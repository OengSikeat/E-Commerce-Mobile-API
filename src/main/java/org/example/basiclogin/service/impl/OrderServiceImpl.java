package org.example.basiclogin.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.basiclogin.exception.BadRequestException;
import org.example.basiclogin.exception.NotFoundException;
import org.example.basiclogin.model.Entity.AppUser;
import org.example.basiclogin.model.Entity.Order;
import org.example.basiclogin.model.Entity.Product;
import org.example.basiclogin.model.Request.OrderRequest;
import org.example.basiclogin.model.Response.AppUserResponse;
import org.example.basiclogin.model.Response.OrderResponse;
import org.example.basiclogin.model.Response.ProductResponse;
import org.example.basiclogin.repository.AppUserRepository;
import org.example.basiclogin.repository.OrderRepository;
import org.example.basiclogin.repository.ProductRepository;
import org.example.basiclogin.service.OrderService;
import org.example.basiclogin.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Set<String> ALLOWED_STATUSES = Set.of("pending", "shipped", "delivered");

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AppUserRepository appUserRepository;

    private AppUserResponse toUserResponse(AppUser user) {
        if (user == null) return null;
        return AppUserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private ProductResponse toProductResponse(Product p) {
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

    private OrderResponse toResponse(Order o, AppUser user, Product product) {
        if (o == null) return null;
        return OrderResponse.builder()
                .id(o.getId())
                .user(toUserResponse(user))
                .product(toProductResponse(product))
                .quantity(o.getQuantity())
                .totalAmount(o.getTotalAmount())
                .status(o.getStatus())
                .createdAt(o.getCreatedAt())
                .build();
    }

    private void validateStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new BadRequestException("Status is required");
        }
        String normalized = status.trim().toLowerCase();
        if (!ALLOWED_STATUSES.contains(normalized)) {
            throw new BadRequestException("Invalid status. Allowed values: pending, shipped, delivered");
        }
    }

    private BigDecimal calculateTotal(Product product, int quantity) {
        if (product.getPrice() == null) {
            throw new BadRequestException("Product price is missing");
        }
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public OrderResponse create(OrderRequest request) {
        validateStatus(request.getStatus());

        Long currentUserId = SecurityUtils.currentUserId();
        AppUser user = appUserRepository.getUserByIdLite(currentUserId);
        if (user == null) throw new NotFoundException("User not found");

        Product product = productRepository.findById(request.getProductId());
        if (product == null) throw new NotFoundException("Product not found");

        int qty = request.getQuantity() == null ? 0 : request.getQuantity();
        if (qty < 1) throw new BadRequestException("Quantity must be at least 1");

        BigDecimal total = calculateTotal(product, qty);
        Order created = orderRepository.create(user.getId(), product.getId(), qty, total, request.getStatus().trim().toLowerCase());
        return toResponse(created, user, product);
    }

    @Override
    public List<OrderResponse> getAll() {
        return orderRepository.findAll().stream().map(order -> {
            AppUser user = appUserRepository.getUserByIdLite(order.getUserId());
            Product product = productRepository.findById(order.getProductId());
            return toResponse(order, user, product);
        }).toList();
    }

    @Override
    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id);
        if (order == null) throw new NotFoundException("Order not found");

        AppUser user = appUserRepository.getUserByIdLite(order.getUserId());
        Product product = productRepository.findById(order.getProductId());
        return toResponse(order, user, product);
    }

    @Override
    public OrderResponse update(Long id, OrderRequest request) {
        Order existing = orderRepository.findById(id);
        if (existing == null) throw new NotFoundException("Order not found");

        validateStatus(request.getStatus());

        Long currentUserId = SecurityUtils.currentUserId();
        AppUser user = appUserRepository.getUserByIdLite(currentUserId);
        if (user == null) throw new NotFoundException("User not found");

        Product product = productRepository.findById(request.getProductId());
        if (product == null) throw new NotFoundException("Product not found");

        int qty = request.getQuantity() == null ? 0 : request.getQuantity();
        if (qty < 1) throw new BadRequestException("Quantity must be at least 1");

        BigDecimal total = calculateTotal(product, qty);
        Order updated = orderRepository.update(id, user.getId(), product.getId(), qty, total, request.getStatus().trim().toLowerCase());
        return toResponse(updated, user, product);
    }

    @Override
    public void delete(Long id) {
        Order existing = orderRepository.findById(id);
        if (existing == null) throw new NotFoundException("Order not found");
        orderRepository.delete(id);
    }
}


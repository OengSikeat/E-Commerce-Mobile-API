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

    private AppUser requireUser(Long userId) {
        AppUser user = appUserRepository.getUserByIdLite(userId);
        if (user == null) {
            throw new NotFoundException("User not found");
        }
        return user;
    }

    private Product requireProduct(Long productId) {
        if (productId == null) {
            throw new BadRequestException("Product id is required");
        }
        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new NotFoundException("Product not found");
        }
        return product;
    }

    private Order requireOrder(Long id) {
        if (id == null) {
            throw new BadRequestException("Order id is required");
        }
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new NotFoundException("Order not found");
        }
        return order;
    }

    private int requireQuantity(Integer quantity) {
        int qty = quantity == null ? 0 : quantity;
        if (qty < 1) {
            throw new BadRequestException("Quantity must be at least 1");
        }
        return qty;
    }

    @Override
    public OrderResponse create(OrderRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }
        validateStatus(request.getStatus());

        Long currentUserId = SecurityUtils.currentUserId();
        AppUser user = requireUser(currentUserId);
        Product product = requireProduct(request.getProductId());

        int qty = requireQuantity(request.getQuantity());
        BigDecimal total = calculateTotal(product, qty);

        Order created = orderRepository.create(
                user.getId(),
                product.getId(),
                qty,
                total,
                request.getStatus().trim().toLowerCase()
        );
        return toResponse(created, user, product);
    }

    @Override
    public List<OrderResponse> getAll() {
        return orderRepository.findAll().stream().map(order -> {
            AppUser user = requireUser(order.getUserId());
            Product product = requireProduct(order.getProductId());
            return toResponse(order, user, product);
        }).toList();
    }

    @Override
    public OrderResponse getById(Long id) {
        Order order = requireOrder(id);
        AppUser user = requireUser(order.getUserId());
        Product product = requireProduct(order.getProductId());
        return toResponse(order, user, product);
    }

    @Override
    public OrderResponse update(Long id, OrderRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }
        Order existing = requireOrder(id);

        validateStatus(request.getStatus());

        // Always use the current authenticated user for updates.
        Long currentUserId = SecurityUtils.currentUserId();
        AppUser user = requireUser(currentUserId);

        Product product = requireProduct(request.getProductId());
        int qty = requireQuantity(request.getQuantity());

        BigDecimal total = calculateTotal(product, qty);
        Order updated = orderRepository.update(
                existing.getId(),
                user.getId(),
                product.getId(),
                qty,
                total,
                request.getStatus().trim().toLowerCase()
        );
        return toResponse(updated, user, product);
    }

    @Override
    public void delete(Long id) {
        requireOrder(id);
        orderRepository.delete(id);
    }
}


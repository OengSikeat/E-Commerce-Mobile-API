package org.example.basiclogin.service.impl;

import io.github.tongbora.bakong.dto.BakongRequest;
import io.github.tongbora.bakong.dto.BakongResponse;
import io.github.tongbora.bakong.dto.CheckTransactionRequest;
import io.github.tongbora.bakong.service.BakongService;
import kh.gov.nbc.bakong_khqr.model.KHQRData;
import kh.gov.nbc.bakong_khqr.model.KHQRResponse;
import kh.gov.nbc.bakong_khqr.model.KHQRCurrency;
import lombok.RequiredArgsConstructor;
import org.example.basiclogin.exception.BadRequestException;
import org.example.basiclogin.exception.NotFoundException;
import org.example.basiclogin.model.Entity.AppUser;
import org.example.basiclogin.model.Entity.Order;
import org.example.basiclogin.model.Entity.Product;
import org.example.basiclogin.model.Enum.OrderStatus;
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
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AppUserRepository appUserRepository;
    private final BakongService bakongService;

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
                .category(p.getCategory())
                .discountPercentage(p.getDiscountPercentage())
                .onPromotion(p.getOnPromotion())
                .createdAt(p.getCreatedAt())
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
                .qr(o.getQr())
                .md5(o.getMd5())
                .build();
    }

    private BigDecimal calculateTotal(Product product, int quantity) {
        if (product.getPrice() == null) {
            throw new BadRequestException("Product price is missing");
        }
        
        BigDecimal unitPrice = product.getPrice();
        
        // Apply promotional discount if enabled
        if (Boolean.TRUE.equals(product.getOnPromotion()) && product.getDiscountPercentage() != null) {
            BigDecimal discountFactor = BigDecimal.valueOf(1.0).subtract(
                    product.getDiscountPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
            );
            unitPrice = unitPrice.multiply(discountFactor);
        }
        
        return unitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
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

    private String safeBakongResponseMessage(BakongResponse response) {
        if (response == null) return null;

        // Try multiple possible accessors depending on the library version
        String[] candidates = {"getResponseMessage", "getMessage", "getStatus", "responseMessage", "message", "status"};
        for (String name : candidates) {
            try {
                var method = response.getClass().getMethod(name);
                Object value = method.invoke(response);
                if (value != null) return value.toString();
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private boolean isBakongSuccess(BakongResponse response) {
        if (response == null) return false;

        String msg = safeBakongResponseMessage(response);
        if (msg != null) {
            String normalized = msg.trim();
            if (normalized.equalsIgnoreCase("success")) return true;
        }

        // Fallback: string search on toString() (last resort)
        String asText = String.valueOf(response);
        return asText.toLowerCase().contains("success");
    }

    private static String safeKhqr(KHQRResponse<KHQRData> response) {
        try {
            if (response == null) return null;
            KHQRData data = response.getData();
            if (data == null) return null;
            return data.getQr();
        } catch (Exception e) {
            return null;
        }
    }

    private static String safeMd5(KHQRResponse<KHQRData> response) {
        try {
            if (response == null) return null;
            KHQRData data = response.getData();
            if (data == null) return null;
            return data.getMd5();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public OrderResponse create(OrderRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        AppUser currentUser = SecurityUtils.currentUser();
        Product product = requireProduct(request.getProductId());
        int qty = requireQuantity(request.getQuantity());
        BigDecimal totalAmount = calculateTotal(product, qty);

        // Generate Bakong KHQR and persist it into the order.
        // As requested: currency USD, amount uses the computed totalAmount
        final String qr;
        final String md5;
        try {
            double amount = totalAmount.doubleValue();
            if (amount <= 0) {
                throw new BadRequestException("Order amount must be greater than 0");
            }

            BakongRequest bakongRequest = new BakongRequest(
                    KHQRCurrency.USD,
                    amount,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            KHQRResponse<KHQRData> khqrResponse = bakongService.generateQR(bakongRequest);
            qr = safeKhqr(khqrResponse);
            md5 = safeMd5(khqrResponse);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Failed to generate Bakong QR");
        }

        if (qr == null || qr.isBlank() || md5 == null || md5.isBlank()) {
            throw new BadRequestException("Failed to generate Bakong QR");
        }

        Order created = orderRepository.create(
                currentUser.getId(),
                product.getId(),
                qty,
                totalAmount,
                OrderStatus.PENDING.dbValue(),
                qr,
                md5
        );

        return toResponse(created, currentUser, product);
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

        AppUser currentUser = requireUser(existing.getUserId());
        Product product = requireProduct(request.getProductId());
        int qty = requireQuantity(request.getQuantity());

        BigDecimal total = calculateTotal(product, qty);
        Order updated = orderRepository.update(
                existing.getId(),
                existing.getUserId(),
                product.getId(),
                qty,
                total,
                existing.getStatus(),
                existing.getQr(),
                existing.getMd5()
        );
        return toResponse(updated, currentUser, product);
    }

    @Override
    public void delete(Long id) {
        requireOrder(id);
        orderRepository.delete(id);
    }

    @Override
    public OrderResponse updateStatus(Long id, OrderStatus status) {
        if (status == null) throw new BadRequestException("status is required");
        Order existing = requireOrder(id);

        int updatedRows = orderRepository.updateStatus(existing.getId(), status.dbValue());
        if (updatedRows == 0) throw new NotFoundException("Order not found");

        Order updated = requireOrder(id);
        AppUser user = requireUser(updated.getUserId());
        Product product = requireProduct(updated.getProductId());
        return toResponse(updated, user, product);
    }

    @Override
    public OrderResponse updatePaymentInfo(Long id, String qr, String md5) {
        Order existing = requireOrder(id);
        if (qr == null || qr.isBlank()) throw new BadRequestException("qr is required");
        if (md5 == null || md5.isBlank()) throw new BadRequestException("md5 is required");

        int updated = orderRepository.updatePaymentInfo(existing.getId(), qr, md5);
        if (updated == 0) throw new NotFoundException("Order not found");

        Order reloaded = requireOrder(existing.getId());
        AppUser user = requireUser(reloaded.getUserId());
        Product product = requireProduct(reloaded.getProductId());
        return toResponse(reloaded, user, product);
    }

    @Override
    public OrderResponse refreshPaymentStatus(Long id) {
        Order existing = requireOrder(id);
        if (existing.getMd5() == null || existing.getMd5().isBlank()) {
            throw new BadRequestException("Order md5 is missing");
        }

        CheckTransactionRequest check = new CheckTransactionRequest(existing.getMd5());

        BakongResponse response = bakongService.checkTransactionByMD5(check);

        boolean paid = isBakongSuccess(response);
        OrderStatus newStatus = paid ? OrderStatus.PAID : OrderStatus.PENDING;

        orderRepository.updateStatus(existing.getId(), newStatus.dbValue());

        Order updated = requireOrder(existing.getId());
        AppUser user = requireUser(updated.getUserId());
        Product product = requireProduct(updated.getProductId());
        return toResponse(updated, user, product);
    }

    @Override
    public List<OrderResponse> getMyOrders() {
        Long userId = SecurityUtils.currentUserId();
        AppUser user = requireUser(userId);

        return orderRepository.findAllByUserId(userId).stream()
                .map(order -> {
                    Product product = requireProduct(order.getProductId());
                    return toResponse(order, user, product);
                })
                .toList();
    }
}

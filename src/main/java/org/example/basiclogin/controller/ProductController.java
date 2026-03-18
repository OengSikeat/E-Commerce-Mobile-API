package org.example.basiclogin.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.basiclogin.model.Request.DiscountRequest;
import org.example.basiclogin.model.Request.ProductRequest;
import org.example.basiclogin.model.Response.ProductResponse;
import org.example.basiclogin.service.ProductService;
import org.example.basiclogin.service.WishlistService;
import org.example.basiclogin.utils.ApiResponse;
import org.example.basiclogin.utils.BaseResponse;
import org.example.basiclogin.model.Enum.ProductCategory;
import org.example.basiclogin.model.Enum.SortCreatedAt;
import org.example.basiclogin.model.Enum.SortPrice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.example.basiclogin.utils.SecurityUtils;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProductController extends BaseResponse {

    private final ProductService productService;
    private final WishlistService wishlistService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@RequestParam ProductCategory category,
                                                              @Valid @RequestBody ProductRequest request) {
        return responseEntity(true, "Product created", HttpStatus.CREATED,
                productService.create(category, request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll(
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) Boolean newArrivals,
            @RequestParam(required = false) String sortPrice,
            @RequestParam(required = false) String sortCreatedAt,
            @RequestParam(required = false) Boolean trending,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean onPromotion
    ) {
        String categoryValue = category == null ? null : category.name();
        return responseEntity(true, "Products fetched", HttpStatus.OK,
                productService.getAll(categoryValue, newArrivals, sortPrice, sortCreatedAt, null, trending, name, onPromotion));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        return responseEntity(true, "Product fetched", HttpStatus.OK, productService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return responseEntity(true, "Product updated", HttpStatus.OK, productService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/discount")
    public ResponseEntity<ApiResponse<ProductResponse>> discount(@PathVariable Long id,
                                                                @Valid @RequestBody DiscountRequest request) {
        return responseEntity(true, "Discount updated", HttpStatus.OK, productService.updateDiscount(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return responseEntity(true, "Product deleted", HttpStatus.OK);
    }

    @GetMapping("/wishlist")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getWishlistedProducts() {
        Long userId = SecurityUtils.currentUserId();
        return responseEntity(true, "Wishlist fetched", HttpStatus.OK, productService.getWishlistedProducts(userId));
    }

    @PatchMapping("/{id}/wishlist/toggle")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleWishlist(@PathVariable Long id) {
        boolean inWishlist = wishlistService.toggle(id);
        return responseEntity(true, "Wishlist updated", HttpStatus.OK, Map.of("inWishlist", inWishlist));
    }
}

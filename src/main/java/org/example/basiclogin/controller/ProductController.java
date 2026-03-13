package org.example.basiclogin.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.basiclogin.model.Request.DiscountRequest;
import org.example.basiclogin.model.Request.ProductRequest;
import org.example.basiclogin.model.Response.ProductResponse;
import org.example.basiclogin.service.ProductService;
import org.example.basiclogin.service.WaitlistService;
import org.example.basiclogin.utils.ApiResponse;
import org.example.basiclogin.utils.BaseResponse;
import org.example.basiclogin.model.Enum.ProductCategory;
import org.example.basiclogin.model.Enum.SortCreatedAt;
import org.example.basiclogin.model.Enum.SortPrice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProductController extends BaseResponse {

    private final ProductService productService;
    private final WaitlistService waitlistService;

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
            @RequestParam(required = false) SortPrice sortPrice,
            @RequestParam(required = false) SortCreatedAt sortCreatedAt,
            @RequestParam(required = false) Boolean trending,
            @RequestParam(required = false) String name
    ) {
        String categoryValue = category == null ? null : category.name();
        String sortPriceDir = sortPrice == null ? null : sortPrice.toDirection().name().toLowerCase();
        String sortCreatedAtDir = sortCreatedAt == null ? null : sortCreatedAt.toDirection().name().toLowerCase();
        return responseEntity(true, "Products fetched", HttpStatus.OK,
                productService.getAll(categoryValue, newArrivals, sortPriceDir, sortCreatedAtDir, null, trending, name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        return responseEntity(true, "Product fetched", HttpStatus.OK, productService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return responseEntity(true, "Product updated", HttpStatus.OK, productService.update(id, request));
    }

    @PatchMapping("/{id}/discount")
    public ResponseEntity<ApiResponse<ProductResponse>> discount(@PathVariable Long id,
                                                                @Valid @RequestBody DiscountRequest request) {
        return responseEntity(true, "Discount updated", HttpStatus.OK, productService.updateDiscount(id, request));
    }

    @PatchMapping("/{id}/waitlist/toggle")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleWaitlist(@PathVariable Long id) {
        boolean inWaitlist = waitlistService.toggle(id);
        return responseEntity(true, "Waitlist updated", HttpStatus.OK, Map.of("inWaitlist", inWaitlist));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return responseEntity(true, "Product deleted", HttpStatus.OK);
    }
}

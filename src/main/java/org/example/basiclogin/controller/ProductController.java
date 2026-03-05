package org.example.basiclogin.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.basiclogin.model.Request.ProductRequest;
import org.example.basiclogin.model.Response.ProductResponse;
import org.example.basiclogin.service.ProductService;
import org.example.basiclogin.utils.ApiResponse;
import org.example.basiclogin.utils.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProductController extends BaseResponse {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        return responseEntity(true, "Product created", HttpStatus.CREATED, productService.create(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll() {
        return responseEntity(true, "Products fetched", HttpStatus.OK, productService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        return responseEntity(true, "Product fetched", HttpStatus.OK, productService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return responseEntity(true, "Product updated", HttpStatus.OK, productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return responseEntity(true, "Product deleted", HttpStatus.OK);
    }
}

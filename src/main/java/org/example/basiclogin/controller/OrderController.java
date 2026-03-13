package org.example.basiclogin.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.basiclogin.model.Enum.OrderStatus;
import org.example.basiclogin.model.Request.OrderRequest;
import org.example.basiclogin.model.Response.OrderResponse;
import org.example.basiclogin.service.OrderService;
import org.example.basiclogin.utils.ApiResponse;
import org.example.basiclogin.utils.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController extends BaseResponse {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(@Valid @RequestBody OrderRequest request) {
        return responseEntity(true, "Order created", HttpStatus.CREATED, orderService.create(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAll() {
        return responseEntity(true, "Orders fetched", HttpStatus.OK, orderService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getById(@PathVariable Long id) {
        return responseEntity(true, "Order fetched", HttpStatus.OK, orderService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> update(@PathVariable Long id, @Valid @RequestBody OrderRequest request) {
        return responseEntity(true, "Order updated", HttpStatus.OK, orderService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        orderService.delete(id);
        return responseEntity(true, "Order deleted", HttpStatus.OK);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(@PathVariable Long id,
                                                                   @RequestParam OrderStatus status) {
        return responseEntity(true, "Order status updated", HttpStatus.OK,
                orderService.updateStatus(id, status));
    }
}

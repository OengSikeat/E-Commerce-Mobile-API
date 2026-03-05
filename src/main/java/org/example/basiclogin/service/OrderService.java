package org.example.basiclogin.service;

import org.example.basiclogin.model.Request.OrderRequest;
import org.example.basiclogin.model.Response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse create(OrderRequest request);
    List<OrderResponse> getAll();
    OrderResponse getById(Long id);
    OrderResponse update(Long id, OrderRequest request);
    void delete(Long id);
}


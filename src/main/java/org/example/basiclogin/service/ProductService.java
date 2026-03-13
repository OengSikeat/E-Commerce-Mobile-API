package org.example.basiclogin.service;

import org.example.basiclogin.model.Enum.ProductCategory;
import org.example.basiclogin.model.Request.ProductRequest;
import org.example.basiclogin.model.Response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductCategory category, ProductRequest request);

    List<ProductResponse> getAll(String category,
                                Boolean newArrivals,
                                String sortPrice,
                                String sortCreatedAt,
                                Long createdBy,
                                Boolean trending,
                                String name);

    ProductResponse getById(Long id);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);

    ProductResponse updateDiscount(Long id, org.example.basiclogin.model.Request.DiscountRequest request);
}

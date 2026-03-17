package org.example.basiclogin.model.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private AppUserResponse user;
    private ProductResponse product;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String status;
    private String qr;
    private String md5;





























































































}

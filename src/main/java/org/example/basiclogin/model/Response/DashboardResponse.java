package org.example.basiclogin.model.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardResponse {
    private BigDecimal totalRevenue;
    private Long totalOrder;
    private Long totalCustomer;
}


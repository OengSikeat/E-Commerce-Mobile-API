package org.example.basiclogin.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.basiclogin.model.Response.DashboardResponse;
import org.example.basiclogin.repository.DashboardRepository;
import org.example.basiclogin.service.DashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository dashboardRepository;

    @Override
    public DashboardResponse getDashboard() {
        BigDecimal totalRevenue = dashboardRepository.totalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        return DashboardResponse.builder()
                .totalRevenue(totalRevenue)
                .totalOrder(dashboardRepository.totalOrder())
                .totalCustomer(dashboardRepository.totalCustomer())
                .build();
    }
}


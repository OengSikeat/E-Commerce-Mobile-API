package org.example.basiclogin.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface DashboardRepository {

    @Select("SELECT COALESCE(SUM(total_amount), 0) FROM orders")
    BigDecimal totalRevenue();

    @Select("SELECT COUNT(*) FROM orders")
    long totalOrder();

    @Select("SELECT COUNT(*) FROM users")
    long totalCustomer();
}


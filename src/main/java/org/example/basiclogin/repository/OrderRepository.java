package org.example.basiclogin.repository;

import org.apache.ibatis.annotations.*;
import org.example.basiclogin.model.Entity.Order;

import java.util.List;

@Mapper
public interface OrderRepository {

    @Results(id = "orderMapper", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "productId", column = "product_id"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at")
    })
    @Select("""
            SELECT id, user_id, product_id, quantity, total_amount, status, created_at
            FROM orders
            WHERE id = #{id}
            """)
    Order findById(Long id);

    @Select("""
            SELECT id, user_id, product_id, quantity, total_amount, status, created_at
            FROM orders
            ORDER BY id
            """)
    @ResultMap("orderMapper")
    List<Order> findAll();

    @Select("""
            INSERT INTO orders (user_id, product_id, quantity, total_amount, status)
            VALUES (#{userId}, #{productId}, #{quantity}, #{totalAmount}, #{status})
            RETURNING id, user_id, product_id, quantity, total_amount, status, created_at
            """)
    @ResultMap("orderMapper")
    Order create(@Param("userId") Long userId,
                 @Param("productId") Long productId,
                 @Param("quantity") Integer quantity,
                 @Param("totalAmount") java.math.BigDecimal totalAmount,
                 @Param("status") String status);

    @Update("""
            UPDATE orders
            SET user_id = #{userId},
                product_id = #{productId},
                quantity = #{quantity},
                total_amount = #{totalAmount},
                status = #{status}
            WHERE id = #{id}
            RETURNING id, user_id, product_id, quantity, total_amount, status, created_at
            """)
    @ResultMap("orderMapper")
    Order update(@Param("id") Long id,
                 @Param("userId") Long userId,
                 @Param("productId") Long productId,
                 @Param("quantity") Integer quantity,
                 @Param("totalAmount") java.math.BigDecimal totalAmount,
                 @Param("status") String status);

    @Delete("""
            DELETE FROM orders WHERE id = #{id}
            """)
    void delete(Long id);
}


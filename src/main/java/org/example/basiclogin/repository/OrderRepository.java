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
            @Result(property = "address", column = "address"),
            @Result(property = "city", column = "city"),
            @Result(property = "state", column = "state"),
            @Result(property = "country", column = "country"),
            @Result(property = "postcode", column = "postcode"),
            @Result(property = "createdAt", column = "created_at")
    })
    @Select("""
            SELECT id, user_id, product_id, quantity, total_amount, status,
                   address, city, state, country, postcode, created_at
            FROM orders
            WHERE id = #{id}
            """)
    Order findById(Long id);

    @Select("""
            SELECT id, user_id, product_id, quantity, total_amount, status,
                   address, city, state, country, postcode, created_at
            FROM orders
            ORDER BY id
            """)
    @ResultMap("orderMapper")
    List<Order> findAll();

    @Select("""
            INSERT INTO orders (user_id, product_id, quantity, total_amount, status, address, city, state, country, postcode)
            VALUES (#{userId}, #{productId}, #{quantity}, #{totalAmount}, #{status}, #{address}, #{city}, #{state}, #{country}, #{postcode})
            RETURNING id, user_id, product_id, quantity, total_amount, status,
                      address, city, state, country, postcode, created_at
            """)
    @ResultMap("orderMapper")
    Order create(@Param("userId") Long userId,
                 @Param("productId") Long productId,
                 @Param("quantity") Integer quantity,
                 @Param("totalAmount") java.math.BigDecimal totalAmount,
                 @Param("status") String status,
                 @Param("address") String address,
                 @Param("city") String city,
                 @Param("state") String state,
                 @Param("country") String country,
                 @Param("postcode") String postcode);

    @Update("""
            UPDATE orders
            SET user_id = #{userId},
                product_id = #{productId},
                quantity = #{quantity},
                total_amount = #{totalAmount},
                status = #{status},
                address = #{address},
                city = #{city},
                state = #{state},
                country = #{country},
                postcode = #{postcode}
            WHERE id = #{id}
            RETURNING id, user_id, product_id, quantity, total_amount, status,
                      address, city, state, country, postcode, created_at
            """)
    @ResultMap("orderMapper")
    Order update(@Param("id") Long id,
                 @Param("userId") Long userId,
                 @Param("productId") Long productId,
                 @Param("quantity") Integer quantity,
                 @Param("totalAmount") java.math.BigDecimal totalAmount,
                 @Param("status") String status,
                 @Param("address") String address,
                 @Param("city") String city,
                 @Param("state") String state,
                 @Param("country") String country,
                 @Param("postcode") String postcode);

    @Delete("""
            DELETE FROM orders WHERE id = #{id}
            """)
    void delete(Long id);

    @Update("""
            UPDATE orders
            SET status = #{status}
            WHERE id = #{id}
            """)
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}

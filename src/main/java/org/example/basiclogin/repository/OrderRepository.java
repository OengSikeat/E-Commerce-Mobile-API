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
            @Result(property = "qr", column = "qr"),
            @Result(property = "md5", column = "md5"),
            @Result(property = "createdAt", column = "created_at")
    })
    @Select("""
            SELECT id, user_id, product_id, quantity, total_amount, status,
                   qr, md5, created_at
            FROM orders
            WHERE id = #{id}
            """)
    Order findById(Long id);

    @Select("""
            SELECT id, user_id, product_id, quantity, total_amount, status,
                   qr, md5, created_at
            FROM orders
            ORDER BY id
            """)
    @ResultMap("orderMapper")
    List<Order> findAll();

    @Select("""
            INSERT INTO orders (user_id, product_id, quantity, total_amount, status, qr, md5)
            VALUES (#{userId}, #{productId}, #{quantity}, #{totalAmount}, #{status}, #{qr}, #{md5})
            RETURNING id, user_id, product_id, quantity, total_amount, status,
                      qr, md5, created_at
            """)
    @ResultMap("orderMapper")
    Order create(@Param("userId") Long userId,
                 @Param("productId") Long productId,
                 @Param("quantity") Integer quantity,
                 @Param("totalAmount") java.math.BigDecimal totalAmount,
                 @Param("status") String status,
                 @Param("qr") String qr,
                 @Param("md5") String md5);

    @Update("""
            UPDATE orders
            SET user_id = #{userId},
                product_id = #{productId},
                quantity = #{quantity},
                total_amount = #{totalAmount},
                status = #{status},
                qr = #{qr},
                md5 = #{md5}
            WHERE id = #{id}
            RETURNING id, user_id, product_id, quantity, total_amount, status,
                      qr, md5, created_at
            """)
    @ResultMap("orderMapper")
    Order update(@Param("id") Long id,
                 @Param("userId") Long userId,
                 @Param("productId") Long productId,
                 @Param("quantity") Integer quantity,
                 @Param("totalAmount") java.math.BigDecimal totalAmount,
                 @Param("status") String status,
                 @Param("qr") String qr,
                 @Param("md5") String md5);

    @Delete("""
            DELETE FROM orders WHERE id = #{id}
            """)
    void delete(Long id);

    @Update("""
            UPDATE orders
            SET qr = #{qr},
                md5 = #{md5}
            WHERE id = #{id}
            """)
    int updatePaymentInfo(@Param("id") Long id,
                          @Param("qr") String qr,
                          @Param("md5") String md5);

    @Update("""
            UPDATE orders
            SET status = #{status}
            WHERE id = #{id}
            """)
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Select("""
            SELECT id, user_id, product_id, quantity, total_amount, status,
                   qr, md5, created_at
            FROM orders
            WHERE user_id = #{userId}
            ORDER BY id DESC
            """)
    @ResultMap("orderMapper")
    List<Order> findAllByUserId(@Param("userId") Long userId);
}

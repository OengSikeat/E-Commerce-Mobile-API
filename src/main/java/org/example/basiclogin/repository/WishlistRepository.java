package org.example.basiclogin.repository;

import org.apache.ibatis.annotations.*;

@Mapper
public interface WishlistRepository {

    @Insert("""
            INSERT INTO wishlists (user_id, product_id)
            VALUES (#{userId}, #{productId})
            ON CONFLICT (user_id, product_id) DO NOTHING
            """)
    void add(@Param("userId") Long userId, @Param("productId") Long productId);

    @Delete("""
            DELETE FROM wishlists
            WHERE user_id = #{userId} AND product_id = #{productId}
            """)
    int remove(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("""
            SELECT COUNT(*) FROM wishlists
            WHERE user_id = #{userId} AND product_id = #{productId}
            """)
    long exists(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("""
            SELECT COUNT(*) FROM wishlists
            WHERE product_id = #{productId}
            """)
    long countByProductId(@Param("productId") Long productId);
}


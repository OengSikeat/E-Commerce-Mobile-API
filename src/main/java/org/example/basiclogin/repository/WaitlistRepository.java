package org.example.basiclogin.repository;

import org.apache.ibatis.annotations.*;

@Mapper
public interface WaitlistRepository {

    @Select("""
            <script>
            INSERT INTO waitlists (user_id, product_id)
            VALUES (#{userId}, #{productId})
            ON CONFLICT (user_id, product_id) DO NOTHING
            RETURNING id
            </script>
            """)
    Long join(@Param("userId") Long userId, @Param("productId") Long productId);

    @Delete("""
            <script>
            DELETE FROM waitlists
            WHERE user_id = #{userId} AND product_id = #{productId}
            </script>
            """)
    int leave(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("""
            <script>
            SELECT COUNT(*) FROM waitlists
            WHERE user_id = #{userId} AND product_id = #{productId}
            </script>
            """)
    long exists(@Param("userId") Long userId, @Param("productId") Long productId);

    @Select("""
            <script>
            SELECT COUNT(*) FROM waitlists
            WHERE product_id = #{productId}
            </script>
            """)
    long countByProductId(@Param("productId") Long productId);
}


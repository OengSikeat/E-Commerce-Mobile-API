package org.example.basiclogin.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.basiclogin.model.Entity.Product;
import org.example.basiclogin.model.Request.ProductRequest;

import java.util.List;

@Mapper
public interface ProductRepository {

    @Results(id = "productMapper", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "description", column = "description"),
            @Result(property = "price", column = "price"),
            @Result(property = "imageUrl", column = "image_url"),
            @Result(property = "category", column = "category"),
            @Result(property = "discountPercentage", column = "discount_percentage"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "onPromotion", column = "on_promotion"),
            @Result(property = "createdAt", column = "created_at")
    })
    @Select(
            "SELECT id, name, description, price, image_url, category, discount_percentage, created_by, on_promotion, created_at " +
            "FROM products " +
            "WHERE id = #{id}"
    )
    Product findById(Long id);

    @Select("""
            <script>
            SELECT p.id, p.name, p.description, p.price, p.image_url,
                   p.category, p.discount_percentage, p.created_by, p.on_promotion, p.created_at
            FROM products p
            LEFT JOIN wishlists w ON w.product_id = p.id
            <where>
              <if test="category != null and category != ''">
                AND p.category = #{category}
              </if>
              <if test="createdBy != null">
                AND p.created_by = #{createdBy}
              </if>
              <if test="name != null and name != ''">
                AND LOWER(p.name) LIKE CONCAT('%', LOWER(#{name}), '%')
              </if>
              <if test="newArrivals != null and newArrivals == true">
                AND p.created_at &gt;= (CURRENT_TIMESTAMP - INTERVAL '7 days')
              </if>
            </where>
            GROUP BY p.id
            ORDER BY
              <choose>
                <when test="trending != null and trending == true">
                  COUNT(w.id) DESC,
                </when>
                <when test="sortPrice == 'asc'">
                  p.price ASC,
                </when>
                <when test="sortPrice == 'desc'">
                  p.price DESC,
                </when>
                <when test="sortCreatedAt == 'asc'">
                  p.created_at ASC,
                </when>
                <when test="sortCreatedAt == 'desc'">
                  p.created_at DESC,
                </when>
              </choose>
              p.id DESC
            </script>
            """)
    @ResultMap("productMapper")
    List<Product> findAllFiltered(@Param("category") String category,
                                  @Param("newArrivals") Boolean newArrivals,
                                  @Param("sortPrice") String sortPrice,
                                  @Param("sortCreatedAt") String sortCreatedAt,
                                  @Param("createdBy") Long createdBy,
                                  @Param("trending") Boolean trending,
                                  @Param("name") String name);

    @Select(
            "INSERT INTO products (name, description, price, image_url, category, discount_percentage, created_by, on_promotion) " +
                    "VALUES (#{request.name}, #{request.description}, #{request.price}, #{request.imageUrl}, #{category}, 0, #{createdBy}, FALSE) " +
                    "RETURNING id, name, description, price, image_url, category, discount_percentage, created_by, on_promotion, created_at"
    )
    @ResultMap("productMapper")
    Product create(@Param("request") ProductRequest request,
                   @Param("createdBy") Long createdBy,
                   @Param("category") String category);

    @Update(
            "UPDATE products SET " +
                    "name = #{request.name}, " +
                    "description = #{request.description}, " +
                    "price = #{request.price}, " +
                    "image_url = #{request.imageUrl} " +
                    "WHERE id = #{id}"
    )
    int update(@Param("id") Long id, @Param("request") ProductRequest request);

    @Update("""
            UPDATE products
            SET discount_percentage = COALESCE(CAST(#{percentage} AS numeric), discount_percentage),
                on_promotion = CASE WHEN COALESCE(CAST(#{percentage} AS numeric), 0) > 0 THEN TRUE ELSE FALSE END
            WHERE id = #{id}
            """)
    int updateDiscount(@Param("id") Long id, @Param("percentage") java.math.BigDecimal percentage);

    @Delete("DELETE FROM products WHERE id = #{id}")
    void delete(Long id);

    @Select("""
            SELECT p.id, p.name, p.description, p.price, p.image_url,
                   p.category, p.discount_percentage, p.created_by, p.on_promotion, p.created_at
            FROM products p
            INNER JOIN wishlists w ON w.product_id = p.id
            WHERE w.user_id = #{userId}
            ORDER BY w.id DESC
            """)
    @ResultMap("productMapper")
    List<Product> findWishlistedProductsByUserId(@Param("userId") Long userId);
}

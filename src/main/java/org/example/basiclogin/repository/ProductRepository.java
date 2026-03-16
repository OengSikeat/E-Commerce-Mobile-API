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
            @Result(property = "sizeOptions", column = "size_options"),
            @Result(property = "category", column = "category"),
            @Result(property = "discountPercentage", column = "discount_percentage"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "onPromotion", column = "on_promotion"),
            @Result(property = "createdAt", column = "created_at")
    })
    @Select(
            "SELECT id, name, description, price, image_url, size_options, category, discount_percentage, created_by, on_promotion, created_at " +
            "FROM products " +
            "WHERE id = #{id}"
    )
    Product findById(Long id);

    @Select("""
            SELECT p.id, p.name, p.description, p.price, p.image_url, p.size_options,
                   p.category, p.discount_percentage, p.created_by, p.on_promotion, p.created_at
            FROM products p
            LEFT JOIN wishlists w ON w.product_id = p.id
            WHERE (#{category} IS NULL OR #{category} = '' OR p.category = #{category})
              AND (#{createdBy} IS NULL OR p.created_by = #{createdBy})
              AND (#{name} IS NULL OR #{name} = '' OR LOWER(p.name) LIKE CONCAT('%', LOWER(#{name}), '%'))
              AND (#{newArrivals} IS NULL OR #{newArrivals} = FALSE OR p.created_at >= (CURRENT_TIMESTAMP - INTERVAL '7 days'))
            GROUP BY p.id
            ORDER BY
              CASE WHEN COALESCE(#{trending}, FALSE) THEN COUNT(w.id) END DESC,
              CASE WHEN (COALESCE(#{trending}, FALSE) = FALSE AND #{sortPrice} = 'asc') THEN p.price END ASC,
              CASE WHEN (COALESCE(#{trending}, FALSE) = FALSE AND #{sortPrice} = 'desc') THEN p.price END DESC,
              CASE WHEN (COALESCE(#{trending}, FALSE) = FALSE AND (#{sortPrice} IS NULL OR #{sortPrice} = '') AND #{sortCreatedAt} = 'asc') THEN p.created_at END ASC,
              CASE WHEN (COALESCE(#{trending}, FALSE) = FALSE AND (#{sortPrice} IS NULL OR #{sortPrice} = '') AND #{sortCreatedAt} = 'desc') THEN p.created_at END DESC,
              p.id DESC
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
            "INSERT INTO products (name, description, price, image_url, size_options, category, discount_percentage, created_by, on_promotion) " +
                    "VALUES (#{request.name}, #{request.description}, #{request.price}, #{request.imageUrl}, #{request.sizeOptions}, #{category}, 0, #{createdBy}, FALSE) " +
                    "RETURNING id, name, description, price, image_url, size_options, category, discount_percentage, created_by, on_promotion, created_at"
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
                    "image_url = #{request.imageUrl}, " +
                    "size_options = #{request.sizeOptions} " +
                    "WHERE id = #{id} " +
                    "RETURNING id, name, description, price, image_url, size_options, category, discount_percentage, created_by, on_promotion, created_at"
    )
    @ResultMap("productMapper")
    Product update(@Param("id") Long id, @Param("request") ProductRequest request);

    @Update("""
            UPDATE products
            SET discount_percentage = COALESCE(#{percentage}, discount_percentage),
                on_promotion = CASE WHEN COALESCE(#{percentage}, 0) > 0 THEN TRUE ELSE FALSE END
            WHERE id = #{id}
            """)
    int updateDiscount(@Param("id") Long id, @Param("percentage") java.math.BigDecimal percentage);

    @Delete("DELETE FROM products WHERE id = #{id}")
    void delete(Long id);

    @Select("""
            SELECT p.id, p.name, p.description, p.price, p.image_url, p.size_options,
                   p.category, p.discount_percentage, p.created_by, p.on_promotion, p.created_at
            FROM products p
            INNER JOIN wishlists w ON w.product_id = p.id
            WHERE w.user_id = #{userId}
            ORDER BY w.id DESC
            """)
    @ResultMap("productMapper")
    List<Product> findWishlistedProductsByUserId(@Param("userId") Long userId);
}

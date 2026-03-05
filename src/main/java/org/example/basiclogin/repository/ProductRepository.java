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
            @Result(property = "onPromotion", column = "on_promotion")
    })
    @Select(
            "SELECT id, name, description, price, image_url, size_options, on_promotion " +
            "FROM products " +
            "WHERE id = #{id}"
    )
    Product findById(Long id);

    @Select(
            "SELECT id, name, description, price, image_url, size_options, on_promotion " +
            "FROM products " +
            "ORDER BY id"
    )
    @ResultMap("productMapper")
    List<Product> findAll();

    @Select(
            "INSERT INTO products (name, description, price, image_url, size_options, on_promotion) " +
            "VALUES (#{request.name}, #{request.description}, #{request.price}, #{request.imageUrl}, #{request.sizeOptions}, COALESCE(#{request.onPromotion}, FALSE)) " +
            "RETURNING id, name, description, price, image_url, size_options, on_promotion"
    )
    @ResultMap("productMapper")
    Product create(@Param("request") ProductRequest request);

    @Update(
            "UPDATE products SET " +
            "name = #{request.name}, " +
            "description = #{request.description}, " +
            "price = #{request.price}, " +
            "image_url = #{request.imageUrl}, " +
            "size_options = #{request.sizeOptions}, " +
            "on_promotion = COALESCE(#{request.onPromotion}, FALSE) " +
            "WHERE id = #{id} " +
            "RETURNING id, name, description, price, image_url, size_options, on_promotion"
    )
    @ResultMap("productMapper")
    Product update(@Param("id") Long id, @Param("request") ProductRequest request);

    @Delete("DELETE FROM products WHERE id = #{id}")
    void delete(Long id);
}

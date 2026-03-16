package org.example.basiclogin.repository;

import org.apache.ibatis.annotations.*;
import org.example.basiclogin.model.Entity.AppUser;
import org.example.basiclogin.model.Request.AppUserRequest;

import java.util.List;

@Mapper
public interface AppUserRepository {

    @Results(id = "appUserLiteMapper", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "email", column = "email"),
            @Result(property = "fullName", column = "full_name"),
            @Result(property = "password", column = "password"),
            @Result(property = "role", column = "role"),
            @Result(property = "createdAt", column = "created_at")
    })
    @Select("""
                SELECT id, email, full_name, password, role, created_at FROM users
                WHERE id = #{id}
            """)
    AppUser getUserByIdLite(Long id);

    @Results(id = "appUserMapper", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "email", column = "email"),
            @Result(property = "fullName", column = "full_name"),
            @Result(property = "password", column = "password"),
            @Result(property = "role", column = "role"),
            @Result(property = "createdAt", column = "created_at")
    })
    @Select("""
                SELECT id, email, full_name, password, role, created_at FROM users
                WHERE email = #{email}
            """)
    AppUser getUserByEmail(String email);

    @Select("""
                INSERT INTO users (email, full_name, password, role)
                VALUES (#{request.email}, #{request.fullName}, #{request.password}, 'USER')
                RETURNING id, email, full_name, password, role, created_at
            """)
    @ResultMap("appUserMapper")
    AppUser register(@Param("request") AppUserRequest request);

    @Select("""
                SELECT id, email, full_name, password, role, created_at FROM users
                WHERE id = #{id}
            """)
    @ResultMap("appUserMapper")
    AppUser getUserById(Long id);

    @Select("""
                SELECT id, email, full_name, password, role, created_at FROM users
                ORDER BY id
                LIMIT #{size} OFFSET #{offset}
            """)
    @ResultMap("appUserMapper")
    List<AppUser> findAllPaged(@Param("size") int size, @Param("offset") int offset);

    @Select("""
                SELECT COUNT(*) FROM users
            """)
    long countAll();

    @Select("""
                SELECT id, email, full_name, password, role, created_at FROM users
                WHERE 1=0 /* findAllByCreatorId not supported - creators removed */
                ORDER BY id
                LIMIT #{size} OFFSET #{offset}
            """)
    @ResultMap("appUserMapper")
    List<AppUser> findAllByCreatorIdPaged(@Param("creatorId") Long creatorId, @Param("size") int size, @Param("offset") int offset);

    @Select("""
                SELECT 0 /* countByCreatorId not supported - creators removed */
            """)
    long countByCreatorId(Long creatorId);

    @Update("""
                UPDATE users
                SET email = #{email},
                    full_name = #{fullName},
                    password = COALESCE(#{password}, password)
                WHERE id = #{id}
            """)
    int updateProfile(@Param("id") Long id,
                      @Param("email") String email,
                      @Param("fullName") String fullName,
                      @Param("password") String password);

    @Update("""
                UPDATE users SET email = #{request.email}, full_name = #{request.fullName}
                WHERE id = #{id}
                RETURNING id, email, full_name, password, role, created_at
            """)
    @ResultMap("appUserMapper")
    AppUser update(@Param("id") Long id, @Param("request") AppUserRequest request);

    @Delete("""
                DELETE FROM users WHERE id = #{id}
            """)
    void delete(Long id);

}

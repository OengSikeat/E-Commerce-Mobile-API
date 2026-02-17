package org.example.basiclogin.repository;

import org.apache.ibatis.annotations.*;
import org.example.basiclogin.model.Entity.AppUser;
import org.example.basiclogin.model.Request.AppUserRequest;

import java.util.List;

@Mapper
public interface AppUserRepository {

    @Results(id = "appUserLiteMapper", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "fullName", column = "full_name"),
            @Result(property = "email", column = "email"),
            @Result(property = "role", column = "user_id", one = @One(
                    select = "getRoleByUserId"
            ))
    })
    @Select("""
                SELECT * FROM app_users
                WHERE user_id = #{userId}
            """)
    AppUser getUserByIdLite(Long userId);

    @Results(id = "appUserMapper", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "fullName", column = "full_name"),
            @Result(property = "email", column = "email"),
            @Result(property = "role", column = "user_id", one = @One(
                    select = "getRoleByUserId"
            )),
            @Result(property = "createdBy", column = "created_by")
    })
    @Select("""
                SELECT * FROM app_users
                WHERE email = #{email};
            """)
    AppUser getUserByEmail(String email);

    @Select("""
                SELECT name FROM app_roles ar
                INNER JOIN app_user_role ur
                ON ar.role_id = ur.role_id
                WHERE user_id = #{userId}
                LIMIT 1;
            """)
    String getRoleByUserId(Long userId);

    @Select("""
                INSERT INTO app_users (full_name, email, password, created_by)
                VALUES (#{request.username}, #{request.email}, #{request.password}, #{createdBy})
                RETURNING *
            """)
    @ResultMap("appUserMapper")
    AppUser register(@Param("request") AppUserRequest request, @Param("createdBy") Long createdBy);

    @Insert("""
                INSERT INTO app_user_role
                VALUES (#{userId}, #{roleId})
            """)
    void insertUserIdAndRoleId(Long roleId, Long userId);

    @Select("""
                SELECT * FROM app_users
                WHERE user_id = #{userId}
            """)
    @ResultMap("appUserMapper")
    AppUser getUserById(Long userId);

    @Select("""
                SELECT * FROM app_users
                ORDER BY user_id
                LIMIT #{size} OFFSET #{offset}
            """)
    @ResultMap("appUserMapper")
    List<AppUser> findAllPaged(@Param("size") int size, @Param("offset") int offset);

    @Select("""
                SELECT COUNT(*) FROM app_users
            """)
    long countAll();

    @Select("""
                SELECT * FROM app_users
                WHERE created_by = #{creatorId}
                ORDER BY user_id
                LIMIT #{size} OFFSET #{offset}
            """)
    @ResultMap("appUserMapper")
    List<AppUser> findAllByCreatorIdPaged(@Param("creatorId") Long creatorId, @Param("size") int size, @Param("offset") int offset);

    @Select("""
                SELECT COUNT(*) FROM app_users
                WHERE created_by = #{creatorId}
            """)
    long countByCreatorId(Long creatorId);

    @Update("""
                UPDATE app_users SET full_name = #{request.username}, email = #{request.email}
                WHERE user_id = #{userId}
                RETURNING *
            """)
    @ResultMap("appUserMapper")
    AppUser update(@Param("userId") Long userId, @Param("request") AppUserRequest request);

    @Delete("""
                DELETE FROM app_users WHERE user_id = #{userId}
            """)
    void delete(Long userId);

}

package org.example.basiclogin.repository;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Mapper
public interface BlacklistTokenRepository {
    @Results(id = "blacklistedTokenMapper", value = {
            @Result(property = "tokenId", column = "token_id"),
            @Result(property = "token", column = "token"),
            @Result(property = "expiryDate", column = "expiry_date")
    })
    @Insert("""
        INSERT INTO blacklisted_tokens (token, expiry_date)
        VALUES (#{token}, #{expiryDate})
    """)
    void insertToken(String token, Date expiryDate);


    @Select("""
        SELECT COUNT(*) > 0
        FROM blacklisted_tokens
        WHERE token = #{token}
    """)
    boolean existsByToken(@Param("token") String token);

}

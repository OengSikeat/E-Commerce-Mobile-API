package org.example.basiclogin.model.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlacklistedToken {
    private Long tokenId;
    private String token;
    private Date expiryDate;
}

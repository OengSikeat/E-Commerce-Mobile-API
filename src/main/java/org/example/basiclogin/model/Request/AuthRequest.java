package org.example.basiclogin.model.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    @Schema(defaultValue = "admin@gmail.com", example = "admin@gmail.com")
    private String email;
    @Schema(defaultValue = "string", example = "string")
    private String password;
}

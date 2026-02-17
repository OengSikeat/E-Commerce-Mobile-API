package org.example.basiclogin.model.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String role;
    private Long createdBy;
}

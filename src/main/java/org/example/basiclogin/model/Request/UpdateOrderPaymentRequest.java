package org.example.basiclogin.model.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderPaymentRequest {
    @NotBlank(message = "qr is required")
    private String qr;

    @NotBlank(message = "md5 is required")
    private String md5;
}


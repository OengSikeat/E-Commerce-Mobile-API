package org.example.basiclogin.model.Request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountRequest {
    @NotNull(message = "percentage is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "percentage must be >= 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "percentage must be <= 100")
    private BigDecimal percentage;
}


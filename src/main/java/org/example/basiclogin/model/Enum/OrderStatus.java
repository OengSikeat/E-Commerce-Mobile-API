package org.example.basiclogin.model.Enum;

public enum OrderStatus {
    PENDING,
    SHIPPED,
    DELIVERED;

    public String dbValue() {
        return name().toLowerCase();
    }
}


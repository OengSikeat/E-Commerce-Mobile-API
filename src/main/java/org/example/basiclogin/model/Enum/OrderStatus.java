package org.example.basiclogin.model.Enum;

public enum OrderStatus {
    PENDING,
    PAID;

    public String dbValue() {
        return name().toLowerCase();
    }
}

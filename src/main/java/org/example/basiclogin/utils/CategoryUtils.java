package org.example.basiclogin.utils;

import org.example.basiclogin.exception.BadRequestException;
import org.example.basiclogin.model.Enum.ProductCategory;

public final class CategoryUtils {
    private CategoryUtils() {}

    public static String normalizeOrThrow(String category) {
//        if (category == null || category.isBlank()) {
//            return ProductCategory.OTHERS.name();
//        }
        String normalized = category.trim().toUpperCase();
        if (!ProductCategory.ALLOWED.contains(normalized)) {
            throw new BadRequestException("Invalid category. Allowed: " + String.join(", ", ProductCategory.ALLOWED));
        }
        return normalized;
    }
}


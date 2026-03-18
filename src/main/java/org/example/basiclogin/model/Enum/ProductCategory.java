package org.example.basiclogin.model.Enum;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ProductCategory {
    // Fashion
    WOMENS_FASHION,
    MENS_FASHION,
    KIDS_FASHION,
    SHOES,
    BAGS,
    JEWELRY,
    WATCHES,

    // Electronics
    SMARTPHONES,
    LAPTOPS,
    TABLETS,
    CAMERAS,
    AUDIO,
    GAMING,
    ACCESSORIES,

    // Home & Living
    HOME_APPLIANCES,
    HOME_DECOR,
    FURNITURE,
    KITCHEN_DINING,

    // Beauty & Health
    BEAUTY,
    PERSONAL_CARE,

    // Sports & Outdoors
    SPORTS_OUTDOORS;

    // Default / fallback


    public static final Set<String> ALLOWED = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableSet());
}

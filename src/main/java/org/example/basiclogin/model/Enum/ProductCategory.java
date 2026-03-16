package org.example.basiclogin.model.Enum;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ProductCategory {
    // Women
    WOMEN_TOPS,
    WOMEN_DRESSES,
    WOMEN_BOTTOMS,
    WOMEN_OUTERWEAR,
    WOMEN_ACTIVEWEAR,
    WOMEN_INTIMATES,

    // Men
    MEN_TOPS,
    MEN_BOTTOMS,
    MEN_OUTERWEAR,
    MEN_ACTIVEWEAR,
    MEN_UNDERWEAR,

    // Kids
    KIDS_GIRLS,
    KIDS_BOYS,
    KIDS_BABY,

    // Footwear
    SHOES_SNEAKERS,
    SHOES_SANDALS,
    SHOES_BOOTS,

    // Accessories
    BAGS,
    ACCESSORIES,

    OTHERS;

    public static final Set<String> ALLOWED = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableSet());
}

package org.example.basiclogin.model.Enum;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ProductCategory {
    ELECTRONICS,
    PHONES_TABLETS,
    COMPUTERS,
    GAMING,
    HOME_APPLIANCES,
    FASHION_MENS,
    FASHION_WOMENS,
    FASHION_KIDS,
    SHOES,
    BEAUTY,
    HEALTH,
    SPORTS_OUTDOORS,
    TOYS_GAMES,
    BABY,
    GROCERY,
    PET_SUPPLIES,
    BOOKS,
    OFFICE_SUPPLIES,
    HOME_GARDEN,
    AUTOMOTIVE,
    OTHERS;

    public static final Set<String> ALLOWED = Arrays.stream(values())
            .map(Enum::name)
            .collect(Collectors.toUnmodifiableSet());
}


package org.example.basiclogin.model.Enum;

/**
 * Price sorting options (kept explicit for request params).
 */
public enum SortPrice {
    LOWEST,
    HIGHEST;

    public SortDirection toDirection() {
        return this == LOWEST ? SortDirection.ASC : SortDirection.DESC;
    }
}

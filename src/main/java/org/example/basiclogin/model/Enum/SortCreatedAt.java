package org.example.basiclogin.model.Enum;

/**
 * createdAt sorting options for request params.
 */
public enum SortCreatedAt {
    NEWEST,
    OLDEST;

    public SortDirection toDirection() {
        return this == NEWEST ? SortDirection.DESC : SortDirection.ASC;
    }
}

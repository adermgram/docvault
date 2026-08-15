package com.adam.docvault.document.validation;

import com.adam.docvault.validation.SortField;

public enum DocumentSortField implements SortField{
    ORIGINAL_FILENAME("originalFilename"),
    CONTENT_TYPE("contentType"),
    SIZE("size"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    
    private final String property;

    DocumentSortField(String property) {
        this.property = property;
    }

    @Override
    public String getProperty() {
        return property;
    }

}

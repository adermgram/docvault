package com.adam.docvault.validation;

import org.springframework.data.domain.Pageable;

import com.adam.docvault.exception.InvalidRequestException;
import java.util.Arrays;
import org.springframework.data.domain.Sort;

public final class SortValidator {

    private SortValidator() {
    }

    public static void validate(
            Pageable pageable,
            SortField[] allowedFields
    ) {
        for (Sort.Order order : pageable.getSort()) {

        boolean allowed = Arrays.stream(allowedFields)
                .anyMatch(field ->
                        field.getProperty().equals(order.getProperty())
                );

        if (!allowed) {
            throw new InvalidRequestException(
                    "Invalid sort field: " + order.getProperty()
            );
        }
        }
    }
}

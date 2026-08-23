package com.adam.docvault.document.entity;

import java.util.UUID;

import com.adam.docvault.user.entity.User;

public final class DocumentTestFactory {

    private DocumentTestFactory() {
    }

    public static Document create(
            UUID id,
            User owner
    ) {
        return new Document(
                id,
                owner,
                "test-document.pdf",
                "application/pdf",
                1024L,
                "test-storage-key"
        );
    }
}
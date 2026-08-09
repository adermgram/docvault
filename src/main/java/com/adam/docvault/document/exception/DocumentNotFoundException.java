package com.adam.docvault.document.exception;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(){
        super("Document not found");
    }

}
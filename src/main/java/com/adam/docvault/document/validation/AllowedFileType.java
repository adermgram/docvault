package com.adam.docvault.document.validation;

public enum AllowedFileType {
    PDF("application/pdf"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    TXT("text/plain"),
    PNG("image/png"),
    JPEG("image/jpeg");

    private final String mimeType;

    AllowedFileType(String mimeType){
        this.mimeType = mimeType;
    }
    
    public String getMimeType() {
        return mimeType;
    }
}

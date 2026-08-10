package com.adam.docvault.document.dto;

import java.io.InputStream;

public record DocumentDownload(
        InputStream inputStream,
        String contentType,
        String originalFilename,
        long size
) {}

package com.adam.docvault.document.validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.tika.Tika;
import org.apache.tika.detect.Detector;
import org.apache.tika.mime.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.adam.docvault.document.exception.InvalidFileException;
import org.apache.tika.metadata.Metadata;


@Service
public class LocalFileValidator implements FileValidator {
    private final Tika tika;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public LocalFileValidator(Tika tika){
        this.tika = tika;
    }

    @Override
    public String validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException("File too large");
        }


        try (InputStream inputStream = file.getInputStream()) {

            String mimeType = tika.detect(inputStream);

            boolean allowed = Arrays.stream(AllowedFileType.values())
            .anyMatch(type -> type.getMimeType().equals(mimeType));

            if(!allowed){
                throw new InvalidFileException("file type now allowed");
            }

            return mimeType;

        } catch (IOException e) {
            throw new InvalidFileException("Failed to detect file type");
        }
    }

}

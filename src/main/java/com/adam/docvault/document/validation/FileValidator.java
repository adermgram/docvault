package com.adam.docvault.document.validation;

import org.springframework.web.multipart.MultipartFile;

public interface FileValidator {

    String validate(MultipartFile file);

}

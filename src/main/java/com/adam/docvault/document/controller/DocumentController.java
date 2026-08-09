package com.adam.docvault.document.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.adam.docvault.document.dto.DocumentResponseDTO;
import com.adam.docvault.document.service.DocumentService;
import com.adam.docvault.user.entity.User;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService){
        this.documentService = documentService;
    }


    @GetMapping("/{documentId}")
    public DocumentResponseDTO getDocument(@PathVariable UUID documentId, @AuthenticationPrincipal User user){
        return documentService.getDocument(documentId, user);   

    }

    @PostMapping
    public DocumentResponseDTO uploadDocument(@RequestParam("file") MultipartFile file,  @AuthenticationPrincipal User user){
        return documentService.uploadDocument(file, user);
    }
}

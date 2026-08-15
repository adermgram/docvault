package com.adam.docvault.document.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.adam.docvault.document.dto.DocumentDownload;
import com.adam.docvault.document.dto.DocumentResponseDTO;
import com.adam.docvault.document.service.DocumentService;
import com.adam.docvault.user.entity.User;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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

    @GetMapping("/{documentId}/download")
    public ResponseEntity<InputStreamResource> downloadDocument(
            @PathVariable UUID documentId,
            @AuthenticationPrincipal User user
    ) {
        DocumentDownload download =
                documentService.downloadDocument(documentId, user);

        InputStreamResource resource =
                new InputStreamResource(download.inputStream());

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(download.contentType())
                )
                .contentLength(download.size())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                        download.originalFilename() +
                        "\""
                )
                .body(resource);
    }

    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDocument(
            @PathVariable UUID documentId,
            @AuthenticationPrincipal User user
    ) {
        documentService.deleteDocument(documentId, user);
    }

    @GetMapping
    public Page<DocumentResponseDTO> getDocuments(
        @AuthenticationPrincipal User user,
         @RequestParam(required = false) String search,
        Pageable pageable
    ) {
        return documentService.getDocuments(user, search, pageable);
    }
}

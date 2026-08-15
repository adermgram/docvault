package com.adam.docvault.document.service;

import java.io.InputStream;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.adam.docvault.document.entity.Document;
import com.adam.docvault.document.exception.DocumentNotFoundException;
import com.adam.docvault.document.repository.DocumentRepository;
import com.adam.docvault.document.storage.StorageService;
import com.adam.docvault.document.validation.DocumentSortField;
import com.adam.docvault.document.validation.FileValidator;
import com.adam.docvault.user.entity.User;
import com.adam.docvault.validation.SortValidator;
import com.adam.docvault.document.dto.DocumentDownload;
import com.adam.docvault.document.dto.DocumentResponseDTO;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final StorageService storageService;
    private final FileValidator fileValidator;

    public DocumentService(DocumentRepository documentRepository, StorageService storageService, FileValidator fileValidator) {
        this.documentRepository = documentRepository;
        this.storageService = storageService;
        this.fileValidator = fileValidator;
    }

    public DocumentResponseDTO getDocument(UUID documentId, User user) {

        Document document = documentRepository
                .findByIdAndOwnerId(documentId, user.getId())
                .orElseThrow(DocumentNotFoundException::new);

        return toResponseDTO(document);
        
    }

    public DocumentResponseDTO uploadDocument(MultipartFile file, User user){
        String contentType = fileValidator.validate(file);
        String storageKey = storageService.store(file);

        try{
            Document document = new Document(
                user,
                file.getOriginalFilename(),
                contentType,
                file.getSize(),
                storageKey
            );   

            Document savedDocument = documentRepository.save(document);

            return toResponseDTO(savedDocument);

        } catch (RuntimeException e){

            storageService.delete(storageKey);

            throw e;
        }
        

    }

    public DocumentDownload downloadDocument(UUID documentId, User user) {

        Document document = documentRepository
                .findByIdAndOwnerId(documentId, user.getId())
                .orElseThrow(DocumentNotFoundException::new);

        InputStream inputStream = storageService.load(
                document.getStorageKey()
        );

        return new DocumentDownload(
                inputStream,
                document.getContentType(),
                document.getOriginalFilename(),
                document.getSize()
        );
    }


    @Transactional
    public void deleteDocument(UUID documentId, User user){
        
        Document document = documentRepository
                .findByIdAndOwnerId(documentId, user.getId())
                .orElseThrow(DocumentNotFoundException::new);

        documentRepository.delete(document);

        storageService.delete(document.getStorageKey());

    }

    public Page<DocumentResponseDTO> getDocuments(User user, String search, Pageable pageable){

        SortValidator.validate(pageable, DocumentSortField.values());
         
        if (search == null || search.isBlank()) {
            return documentRepository
                .findByOwnerId(user.getId(), pageable)
                .map(this::toResponseDTO);
        }

        return  documentRepository
                .findByOwnerIdAndOriginalFilenameContainingIgnoreCase(user.getId(), search, pageable)
                .map(this::toResponseDTO);
       
    }
    

    private DocumentResponseDTO toResponseDTO(Document document){
         return new DocumentResponseDTO(
                document.getId(),
                document.getOriginalFilename(),
                document.getContentType(),
                document.getSize(),
                document.getCreatedAt(),
                document.getUpdatedAt()
         );
    }
}

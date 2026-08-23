package com.adam.docvault.document.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.any;

import com.adam.docvault.document.dto.DocumentDownload;
import com.adam.docvault.document.dto.DocumentResponseDTO;
import com.adam.docvault.document.entity.Document;
import com.adam.docvault.document.entity.DocumentTestFactory;
import com.adam.docvault.document.exception.DocumentNotFoundException;
import com.adam.docvault.document.repository.DocumentRepository;
import com.adam.docvault.document.storage.StorageService;
import com.adam.docvault.document.validation.FileValidator;
import com.adam.docvault.exception.InvalidRequestException;
import com.adam.docvault.user.entity.Role;
import com.adam.docvault.user.entity.User;
import com.adam.docvault.user.entity.UserTestFactory;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private FileValidator fileValidator;

    @InjectMocks
    private DocumentService documentService;


    @Test
    void shouldGetDocumentForOwner() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();

        User user = UserTestFactory.create(userId, Role.USER);
        Document document = DocumentTestFactory.create(documentId, user);
        when(documentRepository.findByIdAndOwnerId(documentId, userId))
            .thenReturn(Optional.of(document));

        // Act
        DocumentResponseDTO response = documentService.getDocument(documentId, user);
        // Assert

        assertEquals(documentId, response.id());
        assertEquals("test-document.pdf", response.originalFilename());
        assertEquals("application/pdf", response.contentType());
        assertEquals(1024L, response.size());
    }


    @Test
    void shouldThrowDocumentNotFound(){
        //Arrange
        UUID userId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();

        User user = UserTestFactory.create(userId, Role.USER);

        when(documentRepository.findByIdAndOwnerId(documentId, userId))
                .thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(
                DocumentNotFoundException.class,
                () -> documentService.getDocument(documentId, user)
        );

    }

    @Test
    void shouldDownloadDocumentForOwner() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();

        User user = UserTestFactory.create(userId, Role.USER);
        Document document = DocumentTestFactory.create(documentId, user);
        InputStream inputStream = new ByteArrayInputStream("test file".getBytes(StandardCharsets.UTF_8));

        when(documentRepository.findByIdAndOwnerId(documentId, userId))
                .thenReturn(Optional.of(document));

        when(storageService.load(document.getStorageKey()))
                .thenReturn(inputStream);

        // Act
        
        DocumentDownload download = documentService.downloadDocument(documentId, user);
        
        // Assert
        assertSame(inputStream, download.inputStream());
        assertEquals("test-document.pdf", download.originalFilename());
        assertEquals("application/pdf", download.contentType());
        assertEquals(1024L, download.size());

    }

    @Test
    void shouldThrowDocumentNotFoundWhenDownloading() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();

        User user = UserTestFactory.create(userId, Role.USER);

        when(documentRepository.findByIdAndOwnerId(documentId, userId))
                .thenReturn(Optional.empty());
        // Act + Assert
        

        assertThrows(
                DocumentNotFoundException.class,
                () -> documentService.downloadDocument(documentId, user)
        );

        // Verify storage was never touched
        verifyNoInteractions(storageService);
    }

    @Test
    void shouldDeleteDocumentForOwner() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();

        User user = UserTestFactory.create(userId, Role.USER);
        Document document = DocumentTestFactory.create(documentId, user);

        when(documentRepository.findByIdAndOwnerId(documentId, userId))
            .thenReturn(Optional.of(document));

        // Act
        documentService.deleteDocument(documentId, user);

        // Verify
        verify(documentRepository).delete(document);
        verify(storageService).delete(document.getStorageKey());
    }


    @Test
    void shouldNotDeleteWhenDocumentNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();

        User user = UserTestFactory.create(userId, Role.USER);

        when(documentRepository.findByIdAndOwnerId(documentId, userId))
                .thenReturn(Optional.empty());
        // Act + Assert
        

        assertThrows(
                DocumentNotFoundException.class,
                () -> documentService.deleteDocument(documentId, user)
        );

        // Verify no deletion occurred
        verify(documentRepository, never()).delete(any(Document.class));
        verifyNoInteractions(storageService);
    }


    @Test
    void shouldUploadDocumentSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();


        User user = UserTestFactory.create(userId, Role.USER);


        MultipartFile file = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "test content".getBytes(StandardCharsets.UTF_8)
        );

        when(fileValidator.validate(file))
            .thenReturn("application/pdf");
        when(storageService.store(file))
            .thenReturn("test-storage-key");
        when(documentRepository.save(any(Document.class)))
         .thenAnswer(invocation -> invocation.getArgument(0));


        // Act
        DocumentResponseDTO response = documentService.uploadDocument(file, user);

        // Assert
        assertEquals("test-document.pdf", response.originalFilename());
        assertEquals("application/pdf", response.contentType());
        assertEquals(file.getSize(), response.size());


        //verify
        verify(fileValidator).validate(file);
        verify(storageService).store(file);
        verify(documentRepository).save(any(Document.class));
    }


    @Test
    void shouldDeleteStoredFileWhenDatabaseSaveFails() {
        // Arrange

        UUID userId = UUID.randomUUID();


        User user = UserTestFactory.create(userId, Role.USER);


        MultipartFile file = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "test content".getBytes(StandardCharsets.UTF_8)
        );

        when(fileValidator.validate(file))
                .thenReturn("application/pdf");

        when(storageService.store(file))
                .thenReturn("test-storage-key");

        RuntimeException databaseException =
                new RuntimeException("Database failure");

        when(documentRepository.save(any(Document.class)))
                .thenThrow(databaseException);

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> documentService.uploadDocument(file, user)
        );

        assertSame(databaseException, exception);

        // Verify cleanup
        verify(storageService).delete("test-storage-key");
    }


    @Test
    void shouldNotStoreFileWhenValidationFails() {
        // Arrange
        UUID userId = UUID.randomUUID();


        User user = UserTestFactory.create(userId, Role.USER);


        MultipartFile file = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "test content".getBytes(StandardCharsets.UTF_8)
        );

        when(fileValidator.validate(file))
            .thenThrow(new InvalidRequestException("Invalid file"));
        


        // Act + Assert

        assertThrows(
                InvalidRequestException.class,
                () -> documentService.uploadDocument(file, user)
        );

        // Verify nothing was stored
        verifyNoInteractions(storageService);
        verifyNoInteractions(documentRepository);
        
    }
}
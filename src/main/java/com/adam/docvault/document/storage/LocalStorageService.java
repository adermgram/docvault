package com.adam.docvault.document.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.adam.docvault.document.exception.StorageException;

@Service
public class LocalStorageService implements StorageService {

    private final Path storageLocation;

    public LocalStorageService(@Value("${storage.location}") String location) {
        this.storageLocation = Path.of(location);
    }

    @Override
    public String store(MultipartFile file) {

        String extension = extractExtension(file.getOriginalFilename());
        String storageKey = UUID.randomUUID() + extension;

        try {
            Files.createDirectories(storageLocation);

            Path destination = storageLocation.resolve(storageKey);

            Files.copy(file.getInputStream(), destination);

            return storageKey;

        } catch (IOException e) {
            throw new StorageException("Failed to store file", e);
        }
    }

    private String extractExtension(String filename) {

        if (filename == null || filename.isBlank()) {
            return "";
        }

        int lastDot = filename.lastIndexOf('.');

        if (lastDot == -1 || lastDot == filename.length() - 1) {
            return "";
        }

        return filename.substring(lastDot);
    }

    @Override
    public void delete(String storageKey) {
        Path destination = storageLocation.resolve(storageKey);
        try {
            Files.deleteIfExists(destination);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file", e);
        }
    }
}
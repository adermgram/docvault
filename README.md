# DocVault

DocVault is a secure document management backend built with **Java, Spring Boot, Spring Security, JPA/Hibernate, and PostgreSQL**.

The project is being developed incrementally with a focus on clean architecture, security, separation of concerns, and understanding how Spring Boot works internally.

## Current Features

### Authentication & Security

* User registration
* User login
* BCrypt password hashing
* JWT-based authentication
* JWT signature and expiration validation
* Authentication through Spring Security's `SecurityContext`
* Role-based authorities
* Method-level security with `@PreAuthorize`
* Authenticated user injection with `@AuthenticationPrincipal`
* Protected API endpoints

### Document Management

* Upload documents using `multipart/form-data`
* Server-generated UUID-based storage keys
* Local filesystem document storage
* Original filename preservation
* Content type and file size metadata
* Document ownership tied to the authenticated user
* Owner-restricted document retrieval
* Document metadata returned through DTOs
* Automatic cleanup of stored files when database persistence fails

## Architecture

The application follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

File storage is abstracted separately:

```text
DocumentService
      │
      ├── DocumentRepository
      │
      └── StorageService
              ↓
        LocalStorageService
              ↓
         Local Filesystem
```

This abstraction allows the storage implementation to be replaced later without changing the document business logic.

## Document Ownership

Documents are associated with the authenticated user who uploaded them.

Document retrieval uses an ownership-aware repository query:

```java
Optional<Document> findByIdAndOwnerId(UUID id, UUID userId);
```

This prevents a user from accessing another user's document simply by knowing its UUID.

If a document doesn't exist or doesn't belong to the authenticated user, the API returns `404 Document Not Found`.

## Storage Design

Uploaded files are **not stored directly in PostgreSQL**.

PostgreSQL stores document metadata such as:

```text
id
owner
originalFilename
contentType
size
storageKey
createdAt
updatedAt
```

The actual file is stored separately on the filesystem using a server-generated UUID-based storage key.

For example:

```text
Original filename:
My Final Year Project.pdf

Storage key:
8f3a4c91-7c2e-4c1f-b2d8-example.pdf
```

The original filename is retained for user-facing purposes while the storage key remains an internal implementation detail.

## API

### Authentication

```text
POST /api/auth/register
POST /api/auth/login
```

### Documents

```text
POST /api/documents
GET  /api/documents/{documentId}
GET /api/documents/{documentId}/download
DELETE /api/documents/{documentId}
```

### Upload

Documents are uploaded as `multipart/form-data`.

The request contains a file field named:

```text
file
```

The authenticated user is determined by the JWT rather than being supplied by the client.

## Technology Stack

* Java
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
* Hibernate
* PostgreSQL
* Maven
* Local filesystem storage

## Project Status

🚧 **In active development**

Current focus:

* Authentication and authorization
* Secure document ownership
* Document upload and storage
* Document metadata management

Planned features include document validation, improved storage handling, pagination, search, testing, and additional security hardening.

## Development Philosophy

DocVault is being built incrementally with an emphasis on understanding the reasoning behind each architectural decision rather than simply assembling framework features.

Key principles include:

* Separation of concerns
* Dependency injection
* DTO-based API responses
* Server-controlled ownership and storage identifiers
* Authorization at the appropriate layer
* Reusable abstractions
* Avoiding unnecessary duplication
* Keeping controllers thin
* Letting Spring handle framework responsibilities where appropriate

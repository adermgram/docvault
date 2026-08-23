# DocVault

DocVault is a secure document management backend built with **Java, Spring Boot, Spring Security, JPA/Hibernate, and PostgreSQL**.

The project was built incrementally as a learning project focused on understanding Spring Boot architecture, authentication, authorization, persistence, file storage, API design, and automated testing.


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
* Custom `401 Unauthorized` responses
* Custom `403 Forbidden` responses
* Protected API endpoints
* Prevention of unauthorized document access

### Document Management

* Upload documents using `multipart/form-data`
* Server-generated UUID-based storage keys
* Local filesystem document storage
* Original filename preservation
* Content type and file size metadata
* Document ownership tied to the authenticated user
* Owner-restricted document retrieval
* Owner-restricted document listing
* Paginated document listing
* Case-insensitive filename search
* Document sorting
* Document sort-field allowlisting
* Maximum page-size protection
* Document metadata returned through DTOs
* Download documents
* Delete documents
* Automatic cleanup of stored files when database persistence fails

### File Validation

* Empty-file validation
* Maximum file-size validation
* Apache Tika-based file type detection
* MIME type validation based on detected file content
* Allowlisted file types
* Protection against manually manipulated client-provided content types
* Multipart upload size protection
* Custom `413 Payload Too Large` handling,

Supported file types currently include:

```text
PDF
DOCX
TXT
PNG
JPEG
```

### Admin User Management
* Promote users to ADMIN
* Demote administrators to regular users
* Prevent administrators from demoting themselves
* Prevent demotion of the last remaining administrator
* Prevent promoting an already-admin user
* Paginated user listing
* User search by first name, last name, or email
* Case-insensitive user search
* User sorting
* User sort-field allowlisting
* Maximum page-size protection
* Passwords excluded from administrator responses

### API Documentation

- OpenAPI documentation
- Swagger UI
- JWT Bearer authentication support in Swagger UI

### Testing

The project includes unit tests using **JUnit 5 and Mockito** covering important service-layer behavior.

Current test suite:

```text
Tests run: 18
Failures: 0
Errors: 0
Skipped: 0

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

and 

```java
Page<Document> findByOwnerId(UUID userId, Pageable pageable);
```

This prevents a user from accessing or listing another user's documents simply by knowing their UUID.

Document listing always derives the owner ID from the authenticated user's security context rather than accepting an owner ID from the client.

If a document doesn't exist or doesn't belong to the authenticated user, the API returns:

```text
404 Document Not Found
```

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

### Pagination, Search & Sorting
Pagination, Search & Sorting

```text
GET /api/documents?page=0&size=10
```

Filename search:
```text
GET /api/documents?search=report
```

Sorting:
```text
GET /api/documents?sort=createdAt,desc
```

Search and sorting can also be combined:
```text
GET /api/documents?search=report&sort=originalFilename,asc&page=0&size=10
```
Admin user listing provides similar pagination, search, and sorting functionality.

### Sort Field Allowlisting
Client-provided sort fields are validated against application-defined allowlists.

For example, document sorting currently allows:
```text
originalFilename
contentType
size
createdAt
updatedAt
```
Internal fields such as `storageKey` and `owner` are not exposed as sortable API fields.

Sort validation is implemented through a reusable `SortField` abstraction and `SortValidator`, allowing different resources to define their own supported sort fields without duplicating validation logic.

### Page Size Protection
API endpoints enforce a maximum page size to prevent unreasonable requests.

The current maximum page size is:
```text
50
```

Requests exceeding this limit return:
```text
400 Bad Request
```

## API

### Authentication

```text
POST /api/auth/register
POST /api/auth/login
```

### Documents

```text
POST /api/documents
GET /api/documents
GET  /api/documents/{documentId}
GET /api/documents/{documentId}/download
DELETE /api/documents/{documentId}
```

Document listing supports:
```text
GET /api/documents?type=pdf
GET /api/documents?search=doesnotexist&type=PDF
GET /api/documents?type=PDF&search=report&page=0&size=10&sort=createdAt,desc
```

### Admin
All admin routes are protected with administrator authorization.

```text
PATCH /api/admin/users/{userId}/promote
PATCH /api/admin/users/{userId}/demote
GET   /api/admin/users
```

Admin user listing supports:
```text
GET /api/admin/users?page=0&size=10
GET /api/admin/users?search=adam
GET /api/admin/users?sort=createdAt,desc
```
Search, pagination, and sorting can be combined.

### Upload

Documents are uploaded as `multipart/form-data`.

The request contains a file field named:

```text
file
```

### Swagger UI

When the application is running, Swagger UI is available at:

```text
/swagger-ui/index.html
```

The OpenAPI specification is available at:

```text
/v3/api-docs
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

✅ **Completed as a Spring Boot learning projec**

Current focus:

* Authentication and authorization
* Secure document ownership
* Document upload and storage
* Document validation
* Document metadata management
* User administration
* Pagination, search, and sorting
* API security hardening

Planned features include improved storage handling, automated testing, additional security hardening, and further document management functionality.

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
* Validating client-controlled input
* Enforcing security at the data-access boundary where appropriate



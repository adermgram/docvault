package com.adam.docvault.document.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.adam.docvault.user.entity.User;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @UuidGenerator
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String contentType;
    
    private long size;

    private String originalFilename;

    private String storageKey;
    
    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;


    protected Document(){}

    public Document(
        User owner,
        String originalFilename,
        String contentType,
        long size,
        String storageKey
    ){
        this.owner = owner;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.size = size;
        this.storageKey = storageKey;

    }

    public UUID getId(){
        return id;
    }

    public User getOwner(){
        return owner;
    }

    public String getContentType(){
        return contentType;
    }

    public long getSize(){
        return size;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getStorageKey(){
        return storageKey;
    }

    public Instant getCreatedAt(){
        return createdAt;
    }

    public Instant getUpdatedAt(){
        return updatedAt;
    }
}
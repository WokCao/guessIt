package com.FoZ.guessIt.Models;

import com.FoZ.guessIt.Enumerations.AuthProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
public class UserModel {
    public UserModel() {}
    public UserModel(String email, String fullName, String role, AuthProvider provider, String providerId, String imageUrl, boolean accountVerified) {
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.imageUrl = imageUrl;
        this.accountVerified = accountVerified;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    private String role;

    @Enumerated(EnumType.STRING)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private AuthProvider provider;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String providerId;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @JsonIgnore
    private boolean accountVerified = false;

    @Column(name = "created_at", updatable = false)
    @JsonIgnore
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @JsonIgnore
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

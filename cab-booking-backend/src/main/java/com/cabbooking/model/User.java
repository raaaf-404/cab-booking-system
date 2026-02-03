package com.cabbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

import com.cabbooking.model.enums.UserRole;
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_mobile", columnList = "mobile")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User extends  BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String password;

    @NotBlank
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Builder.Default
    @Column(name="is_active", nullable = false)
    private Boolean isActive = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

    // --- Helper Methods ---

    /**
     * Use this for business logic to check if a user has access.
     */
    public boolean hasRole(UserRole role) {
        return roles != null && roles.contains(role);
    }

    public void addRole(UserRole role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }

    /**
     * Prevents external classes from modifying the set directly.
     */
    public Set<UserRole> getRoles() {
        return roles == null ? Set.of() : Collections.unmodifiableSet(roles);
    }
}
    package com.cabbooking.model;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import lombok.ToString;
    
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;
    
    import org.springframework.data.jpa.domain.support.AuditingEntityListener;
    
    import com.fasterxml.jackson.annotation.JsonIgnore;
    
    import java.time.LocalDateTime;
    import java.util.HashSet;
    import java.util.Set;
    import java.util.Collections;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString(exclude = "password")
    @Entity
    @Table(name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "phone")
    })
    @EntityListeners(AuditingEntityListener.class)

    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;


        @Column(nullable = false)
        private String name;


        @Column(nullable = false, unique = true)
        private String email;

        @Column(nullable = false)
        @JsonIgnore
        private String password;

        @Column(nullable = false, unique = true)
        private String phone;

        @Column(name = "profile_picture")
        private String profilePicture;

        @Column(name = "is_active", columnDefinition = "boolean default true")
        private Boolean isActive = true;

            @ElementCollection(fetch = FetchType.EAGER)
            @CollectionTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"))

            @Column(name = "role")
            private Set<String> roles = new HashSet<>();

        @CreationTimestamp
        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updated_at")
        private LocalDateTime updatedAt;


        //Custom constructor without id and timestamps
        public User(String name, String email, String password, String phone) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.phone = phone;
        }

        //Helper method to add role
        public void addRole(String role) {
            this.roles.add(role);

        }

        public void setRole(Set<String> roles) {
            this.roles = new HashSet<>(roles); 
        }

        public Set<String> getRoles() {
            return Collections.unmodifiableSet(roles);
        }
        

    }




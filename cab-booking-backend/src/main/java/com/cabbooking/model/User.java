    package com.cabbooking.model;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;
    import lombok.ToString;
    
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;

    
    import org.springframework.data.jpa.domain.support.AuditingEntityListener;
    import org.springframework.data.annotation.CreatedDate;
    import  org.springframework.data.annotation.LastModifiedDate;
    import org.springframework.data.annotation.CreatedBy;
    import org.springframework.data.annotation.LastModifiedBy;

    
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

        public enum Role {
           USER,
           DRIVER,
          ADMIN
        }

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
            private Set<Role> roles = new HashSet<>();


        @CreatedBy
        @Column(name = "created_by", updatable = false)
        private String createdBy;

        @CreationTimestamp
        @CreatedDate
        @Column(name = "created_at", updatable = false)
        private LocalDateTime createdAt;

        @LastModifiedBy
        @Column(name = "last_modified_by", updatable = false)
        private String lastModifiedBy;
        

        @UpdateTimestamp
        @LastModifiedDate
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
        public void addRole(Role role) {
            this.roles.add(role);

        }

        public void setRole(Set<Role> roles) {
            this.roles = new HashSet<>(roles); 
        }

        public Set<Role> getRole() {
            return Collections.unmodifiableSet(roles);
        }
        

    }




package com.cabbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(name = "unique_trip_reviewer_type", columnNames = {"trip_id", "reviewer_type"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {

    public enum ReviewerType {
        PASSENGER, DRIVER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    @NotNull(message = "Review must be linked to a specific trip")
    private Trip trip;

    @Enumerated(EnumType.STRING)
    @Column(name = "reviewer_type", nullable = false)
    @NotNull
    private ReviewerType reviewerType;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Column(nullable = false)
    private Integer rating;

    @NotBlank(message = "Comment cannot be empty")
    @Column(columnDefinition = "TEXT")
    private String comment;
}
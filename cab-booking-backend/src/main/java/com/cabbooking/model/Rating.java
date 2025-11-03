package com.cabbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.*;

@Entity
@Table(
        name = "ratings",
        indexes = {
                @Index(name = "idx_rating_driver", columnList = "driver_id"),
                @Index(name = "idx_rating_user", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"ride", "user", "driver"}) // avoid lazy-loading in toString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long ratingId;

    /**
     * One-to-one: ensures only one rating per ride (DB unique constraint).
     * Note: Some JPA providers will still fetch OneToOne eagerly unless bytecode enhancement is enabled.
     * Consider making Ride the owner (mappedBy) if you need reliable lazy-loading without enhancement.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_id", nullable = false, unique = true)
    private Ride ride;

    /**
     * The passenger / user who gave the rating.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The driver who received the rating. Uses the same User entity type.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @NotNull
    @Min(value = 1, message = "rating must be at least 1")
    @Max(value = 5, message = "rating must be at most 5")
    @Column(nullable = false)
    private Integer rating;

    @Lob
    private String comment;

    // convenience constructors / builder can be added if helpful
}

package com.cabbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "passengers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Min(1)
    @Max(5)
    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal rating = BigDecimal.valueOf(5.0);

    @Column(name = "total_trips", nullable = false)
    @Builder.Default
    private Integer totalTrips = 0;

    /**
     * Storing payment tokens (Stripe/Razorpay) as a JSON array.
     * Hibernate 6 automatically handles the JSON conversion.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payment_methods", columnDefinition = "jsonb")
    @Builder.Default
    private List<String> paymentMethods = new ArrayList<>();

    // --- Helper Methods ---

    public void incrementTrips() {
        this.totalTrips++;
    }

    public void updateRating(BigDecimal newRating) {
        // Business logic to calculate average could go here or in a Service
        this.rating = newRating;
    }
}

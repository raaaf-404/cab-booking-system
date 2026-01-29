package com.cabbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_trip", columnList = "trip_id"),
        @Index(name = "idx_payment_status", columnList = "status"),
        @Index(name = "idx_payment_ref", columnList = "transaction_ref")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    public enum PaymentMethod {
        CASH, WALLET, CARD
    }

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED, REFUNDED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    @NotNull(message = "Payment must be linked to a trip")
    private Trip trip;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @NotBlank
    @Builder.Default
    @Column(nullable = false, length = 3)
    private String currency = "USD"; // Standardizing to ISO currency codes

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private PaymentStatus status;

    @Column(name = "transaction_ref", unique = true)
    private String transactionRef;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse; // Stores raw JSON from Stripe/Razorpay for debugging

    // --- Helper Methods ---

    public void markAsCompleted(String ref) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionRef = ref;
    }

    public void markAsFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.gatewayResponse = reason;
    }
}
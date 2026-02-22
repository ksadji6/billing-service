package com.esmt.billing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "txn_ref", unique = true, nullable = false)
    private String txnRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "trip_ref")
    private String tripRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TxnType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "balance_before", precision = 12, scale = 2)
    private BigDecimal balanceBefore;

    @Column(name = "balance_after", precision = 12, scale = 2)
    private BigDecimal balanceAfter;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TxnStatus status = TxnStatus.SUCCESS;

    @Column(name = "is_fallback")
    private boolean isFallback = false;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.txnRef == null) {
            this.txnRef = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
package com.esmt.billing.entity;

import com.esmt.billing.client.NotificationClient;
import jakarta.persistence.*;
import com.esmt.billing.entity.Transaction;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "daily_spent", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal dailySpent = BigDecimal.ZERO;

    @Column(name = "last_reset_date")
    @Builder.Default
    private LocalDate lastResetDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    public void debit(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) throw new RuntimeException("Solde insuffisant");
        this.balance = this.balance.subtract(amount);
        resetDailyIfNeeded();
        this.dailySpent = this.dailySpent.add(amount);
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    private void resetDailyIfNeeded() {
        if (!LocalDate.now().equals(lastResetDate)) {
            this.dailySpent = BigDecimal.ZERO;
            this.lastResetDate = LocalDate.now();
        }
    }
}
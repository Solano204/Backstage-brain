package neobank.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String accountNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountType type;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Builder.Default
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @NotBlank
    @Builder.Default
    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "fromAccount", cascade = CascadeType.ALL)
    private Set<Transaction> sentTransactions = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "toAccount", cascade = CascadeType.ALL)
    private Set<Transaction> receivedTransactions = new HashSet<>();

    public enum AccountType {
        CHECKING, SAVINGS, INVESTMENT
    }
}
package com.task.ecommerce.entity;

import com.task.ecommerce.entity.enums.BillStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;      // e.g. "INV-498813353"

    @Column(nullable = false)
    private Integer orderId;           // FK to Order

    @Column
    private String transactionId;      // Paymob transaction id

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String customerEmail;

    @Column
    private String customerPhone;

    @Column(nullable = false)
    private long totalAmountCents;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String htmlSnapshot;       // store rendered HTML for re-send/audit

    private LocalDateTime emailSentAt;
}

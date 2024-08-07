package com.panda.medicineinventorymanagementsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "Inbound_Transaction")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InboundTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false)
    private Integer quantity;

    @Column
    private Integer originalMedicineQuantity;

    @Column
    private Integer updateTransactionQuantity;

    @Column(name = "received_date")
    private Date receivedDate;

    @Column(nullable = false)
    private String supplier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    protected void onCreate() {
        receivedDate = new Date();
    }
}
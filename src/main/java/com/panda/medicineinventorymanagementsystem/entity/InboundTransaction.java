package com.panda.medicineinventorymanagementsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "Inbound_Transaction")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InboundTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer originalMedicineQuantity;

    @Column(nullable = false)
    private Integer updateTransactionQuantity;

    @Column(name = "received_date", nullable = false)
    private Date receivedDate;

    @Column(nullable = false)
    private String supplier;
}
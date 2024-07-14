package com.panda.medicineinventorymanagementsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "Outbound_Transaction")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OutboundTransaction {
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

    @Column(name = "dispatched_date", nullable = false)
    private Date dispatcheddDate;

    @PrePersist
    protected void onCreate() {
        dispatcheddDate = new Date();
    }

    @Column(nullable = false)
    private String supplier;
}
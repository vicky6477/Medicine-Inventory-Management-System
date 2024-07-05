package com.panda.medicineinventorymanagementsystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
@Data
public class OutboundTransactionDTO {
    private Integer id;

    @NotNull(message = "Medicine ID cannot be null")
    private Integer medicineId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private Integer originalMedicineQuantity;

    private Integer updateTransactionQuantity;

    private Date dispatcheddDate;

    @NotBlank(message = "Supplier name is required")
    private String supplier;
}

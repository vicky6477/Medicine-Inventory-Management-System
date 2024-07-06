package com.panda.medicineinventorymanagementsystem.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class MedicineDTO {

    private Integer id;
    @NotBlank(message = "Name must not be blank")
    private String name;
    @Size(max = 255, message = "Description must not exceed 1000 characters")
    private String description;
    private Integer quantity;
    @NotBlank(message = "Type must not be blank")
    @Pattern(regexp = "PRES|OTC|OTHER", message = "Type must be one of the predefined types: PRES, OTC, OTHER")
    private String type;

}

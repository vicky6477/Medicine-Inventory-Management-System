package com.panda.medicineinventorymanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonRequestDTO {
    private String name;

    private String email;

    private String password;

    @Builder.Default
    private String role = "USER";
}

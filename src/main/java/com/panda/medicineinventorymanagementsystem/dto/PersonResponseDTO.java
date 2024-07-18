package com.panda.medicineinventorymanagementsystem.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PersonResponseDTO {

    private Integer id;

    private String name;

    private String email;

    private String role;

}
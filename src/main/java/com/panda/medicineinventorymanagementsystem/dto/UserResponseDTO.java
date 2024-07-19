package com.panda.medicineinventorymanagementsystem.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UserResponseDTO {

    private Integer id;

    private String name;

    private String email;

    private String role;

}
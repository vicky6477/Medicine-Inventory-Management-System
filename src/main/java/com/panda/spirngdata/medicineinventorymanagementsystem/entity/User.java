package com.panda.spirngdata.medicineinventorymanagementsystem.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue
    @Column
    private Integer id;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private Role role;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private String name;

    @Column
    private Integer age;

    @Column
    private String gender;
}

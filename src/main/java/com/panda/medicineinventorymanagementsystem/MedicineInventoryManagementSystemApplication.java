package com.panda.medicineinventorymanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class MedicineInventoryManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicineInventoryManagementSystemApplication.class, args);
    }

}

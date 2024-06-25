package com.panda.medicineinventorymanagementsystem.repository;

import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Integer> {
}

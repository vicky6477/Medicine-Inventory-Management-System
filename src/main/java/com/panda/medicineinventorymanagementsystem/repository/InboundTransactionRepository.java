package com.panda.medicineinventorymanagementsystem.repository;

import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InboundTransactionRepository  extends JpaRepository<InboundTransaction,Integer> {
    boolean existsByMedicineId(Integer medicineId);
    List<InboundTransaction> findByMedicineId(Integer medicineId);
}

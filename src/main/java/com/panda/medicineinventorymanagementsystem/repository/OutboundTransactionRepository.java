package com.panda.medicineinventorymanagementsystem.repository;

import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboundTransactionRepository extends JpaRepository<OutboundTransaction,Integer> {
    boolean existsByMedicineId(Integer medicineId);
    List<OutboundTransaction> findByMedicineId(Integer medicineId);
}

package com.panda.medicineinventorymanagementsystem.repository;


import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutboundTransactionRepository extends JpaRepository<OutboundTransaction,Integer> {
    boolean existsByMedicineId(Integer medicineId);
    Page<OutboundTransaction> findAllByUser(User user, Pageable pageable);
    Optional<OutboundTransaction> findByIdAndUser(Integer id, User user);

}

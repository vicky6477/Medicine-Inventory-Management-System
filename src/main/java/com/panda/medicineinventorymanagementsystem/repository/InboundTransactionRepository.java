package com.panda.medicineinventorymanagementsystem.repository;

import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InboundTransactionRepository  extends JpaRepository<InboundTransaction,Integer> {
    boolean existsByMedicineId(Integer medicineId);
    Optional<InboundTransaction> findByIdAndUser(Integer id, User user);
    Page<InboundTransaction> findAllByUser(User user, Pageable pageable);


}

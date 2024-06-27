package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import com.panda.medicineinventorymanagementsystem.repository.OutboundTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OutboundTransactionService {
    private final OutboundTransactionRepository outboundTransactionRepository;
    private final MedicineRepository medicineRepository;

    //Construct injection
    @Autowired
    public OutboundTransactionService(OutboundTransactionRepository outboundTransactionRepository, MedicineRepository medicineRepository) {
        this.outboundTransactionRepository = outboundTransactionRepository;
        this.medicineRepository = medicineRepository;
    }

    //Create medicines transactions
    @Transactional
    public List<OutboundTransaction> addOutboundTransactions(List<OutboundTransaction> transactions) {
        for (OutboundTransaction transaction : transactions) {
            Medicine medicine = transaction.getMedicine();
            if (medicine != null && medicine.getId() != null) {
                Optional<Medicine> existingMedicine = medicineRepository.findById(medicine.getId());
                if (!existingMedicine.isPresent()) {
                    throw new IllegalStateException("Medicine with ID " + medicine.getId() + " does not exist.");
                }
                // Set the existing medicine to ensure it is managed and any changes like quantity are updated in the database
                transaction.setMedicine(existingMedicine.get());
            } else {
                throw new IllegalStateException("Transaction must have a valid medicine.");
            }
        }
        return outboundTransactionRepository.saveAll(transactions);
    }

    //Retrieve all transactions
    public Page<OutboundTransaction> getAllOutboundTransactions(Pageable pageable) {
        return outboundTransactionRepository.findAll(pageable);
    }

}

package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.repository.InboundTransactionRepository;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InboundTransactionService {
    private final InboundTransactionRepository inboundTransactionRepository;
    private final MedicineRepository medicineRepository;

    //Construct injection
    @Autowired
    public InboundTransactionService(InboundTransactionRepository inboundTransactionRepository, MedicineRepository medicineRepository) {
        this.inboundTransactionRepository = inboundTransactionRepository;
        this.medicineRepository = medicineRepository;
    }

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public List<InboundTransaction> addInboundTransactions(List<InboundTransaction> transactions) {
        // Collect all medicine IDs from the transactions
        Set<Integer> medicineIds = transactions.stream()
                .map(transaction -> transaction.getMedicine().getId())
                .collect(Collectors.toSet());

        // Fetch all medicines once and store them in a map for quick lookup
        Map<Integer, Medicine> medicines = medicineRepository.findAllById(medicineIds)
                .stream()
                .collect(Collectors.toMap(Medicine::getId, Function.identity()));

        // Update the quantities and set the original Medicine object into each transaction
        transactions.forEach(transaction -> {
            Medicine originalMedicine = medicines.get(transaction.getMedicine().getId());
            if (originalMedicine != null) {
                transaction.setOriginalMedicineQuantity(originalMedicine.getQuantity());
                int newQuantity = originalMedicine.getQuantity() + transaction.getQuantity();
                originalMedicine.setQuantity(newQuantity); // Update the quantity
                transaction.setMedicine(originalMedicine);// Important: set the original medicine object
                transaction.setUpdateTransactionQuantity(newQuantity);
            } else {
                throw new IllegalStateException("Medicine with ID " + transaction.getMedicine().getId() + " not found");
            }
        });

        // Save all updated medicines
        medicineRepository.saveAll(medicines.values());

        // Save and return all transactions
        return inboundTransactionRepository.saveAll(transactions);
    }


    //Retrieve all transactions
    public Page<InboundTransaction> getAllInboundTransactions(Pageable pageable) {
        return inboundTransactionRepository.findAll(pageable);
    }

    // Method to retrieve a single transaction by ID
    public InboundTransaction getInboundTransactionById(Integer id) {
        return inboundTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inbound transaction not found with ID: " + id));
    }

    // Method to update a transaction
    @Transactional
    public InboundTransaction updateInboundTransaction(Integer id, InboundTransaction updatedTransaction) {
        InboundTransaction transaction = getInboundTransactionById(id);
        transaction.setQuantity(updatedTransaction.getQuantity());
        transaction.setMedicine(updatedTransaction.getMedicine());
        return inboundTransactionRepository.save(transaction);
    }

    // Method to delete a transaction
    @Transactional
    public void deleteInboundTransaction(Integer id) {
        InboundTransaction transaction = getInboundTransactionById(id);
        inboundTransactionRepository.delete(transaction);
    }

}
package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.dto.InboundTransactionDTO;
import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.repository.InboundTransactionRepository;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
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

    @Transactional
    public List<InboundTransactionDTO> addInboundTransactions(List<InboundTransactionDTO> transactionsDTO) {
        List<InboundTransaction> transactions = transactionsDTO.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        // Collect all medicine IDs from the transactions
        Set<Integer> medicineIds = transactions.stream()
                .map(transaction -> transaction.getMedicine().getId())
                .collect(Collectors.toSet());

        // Fetch all corresponding medicines once and store them in a map for quick access
        Map<Integer, Medicine> medicines = medicineRepository.findAllById(medicineIds)
                .stream()
                .collect(Collectors.toMap(Medicine::getId, Function.identity()));

        // Update the quantities and set the original Medicine object into each transaction
        transactions.forEach(transaction -> {
            Medicine originalMedicine = medicines.get(transaction.getMedicine().getId());
            if (originalMedicine != null) {
                transaction.setOriginalMedicineQuantity(originalMedicine.getQuantity());
                int newQuantity = originalMedicine.getQuantity() + transaction.getQuantity();
                originalMedicine.setQuantity(newQuantity);
                transaction.setMedicine(originalMedicine);
                transaction.setUpdateTransactionQuantity(newQuantity);
            } else {
                throw new IllegalStateException("Medicine with ID " + transaction.getMedicine().getId() + " not found");
            }
        });

        // Save all updated medicines
        medicineRepository.saveAll(medicines.values());

        // Save and return all transactions
        List<InboundTransaction> savedTransactions = inboundTransactionRepository.saveAll(transactions);
        return savedTransactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    //Retrieve all transactions
    public Page<InboundTransactionDTO> getAllInboundTransactions(Pageable pageable) {
        return inboundTransactionRepository.findAll(pageable)
                .map(this::convertToDTO);
    }


    // Retrieve a single transaction by ID
    public InboundTransactionDTO getInboundTransactionById(Integer id) {
        InboundTransaction transaction = inboundTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inbound transaction not found with ID: " + id));
        return convertToDTO(transaction);
    }

    // Retrieve transactions by medicineId
    public List<InboundTransactionDTO> getTransactionsByMedicineId(Integer medicineId) {
        List<InboundTransaction> transactions = inboundTransactionRepository.findByMedicineId(medicineId);
        if (transactions.isEmpty()) {
            throw new RuntimeException("Inbound transactions not found for medicine ID: " + medicineId);
        }
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



/*
    // Method to update a transaction
    @Transactional
    public InboundTransactionDTO updateInboundTransaction(Integer id, InboundTransactionDTO updatedTransactionDTO) {
        InboundTransaction transaction = inboundTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inbound transaction not found with ID: " + id));
        updateEntity(transaction, updatedTransactionDTO);
        InboundTransaction updatedTransaction = inboundTransactionRepository.save(transaction);
        return convertToDTO(updatedTransaction);
    }

    // Method to delete a transaction
    @Transactional
    public void deleteInboundTransaction(Integer id) {
        InboundTransaction transaction = inboundTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + id));
        inboundTransactionRepository.delete(transaction);
    }*/

    private InboundTransactionDTO convertToDTO(InboundTransaction transaction) {
        InboundTransactionDTO dto = new InboundTransactionDTO();
        dto.setId(transaction.getId());
        dto.setMedicineId(transaction.getMedicine().getId());
        dto.setQuantity(transaction.getQuantity());
        dto.setOriginalMedicineQuantity(transaction.getOriginalMedicineQuantity());
        dto.setUpdateTransactionQuantity(transaction.getUpdateTransactionQuantity());
        dto.setReceivedDate(transaction.getReceivedDate());
        dto.setSupplier(transaction.getSupplier());
        return dto;
    }

    private InboundTransaction convertToEntity(InboundTransactionDTO dto) {
        InboundTransaction transaction = new InboundTransaction();
        Medicine medicine = medicineRepository.findById(dto.getMedicineId()).orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + dto.getMedicineId()));
        transaction.setMedicine(medicine);
        transaction.setQuantity(dto.getQuantity());
        transaction.setSupplier(dto.getSupplier());
        return transaction;
    }

/*
    private void updateEntity(InboundTransaction transaction, InboundTransactionDTO dto) {
        Medicine medicine = transaction.getMedicine();

        // Calculate the old and new quantity difference
        int oldQuantity = transaction.getQuantity();
        int newQuantity = dto.getQuantity();
        int quantityDifference = newQuantity - oldQuantity;

        // Update transaction details
        transaction.setQuantity(newQuantity);
        transaction.setSupplier(dto.getSupplier());

        // Update the medicine stock
        int updatedMedicineStock = medicine.getQuantity() + quantityDifference;
        medicine.setQuantity(updatedMedicineStock);

        // Set the updated transaction quantity after modification
        transaction.setUpdateTransactionQuantity(updatedMedicineStock);

        // Log changes (ensure logger is configured)
        logger.info("Updated transaction ID {}: from {} to {}. New medicine stock: {}", transaction.getId(), oldQuantity, newQuantity, updatedMedicineStock);
    }
*/


}
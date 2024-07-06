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

    /**
     * Constructor for InboundTransactionService with dependency injection.
     * @param inboundTransactionRepository repository for accessing inbound transaction data
     * @param medicineRepository repository for accessing medicine data
     */
    @Autowired
    public InboundTransactionService(InboundTransactionRepository inboundTransactionRepository, MedicineRepository medicineRepository) {
        this.inboundTransactionRepository = inboundTransactionRepository;
        this.medicineRepository = medicineRepository;
    }

    /**
     * Adds a list of inbound transactions and updates the stock quantities for the medicines involved.
     * @param transactionsDTO list of InboundTransactionDTO objects representing the new transactions
     * @return list of InboundTransactionDTO objects after saving to database
     * @throws IllegalStateException if a referenced medicine is not found in the database
     */
    @Transactional
    public List<InboundTransactionDTO> addInboundTransactions(List<InboundTransactionDTO> transactionsDTO) {
        // Convert the list of InboundTransactionDTOs to InboundTransaction entities using the convertToEntity method
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


    /**
     * Retrieves all inbound transactions in a paginated format.
     * @param pageable the pagination and sorting information
     * @return Page of InboundTransactionDTO
     */
    public Page<InboundTransactionDTO> getAllInboundTransactions(Pageable pageable) {
        return inboundTransactionRepository.findAll(pageable)
                .map(this::convertToDTO);
    }


    /**
     * Retrieves a single inbound transaction by its ID.
     * @param id the ID of the transaction to retrieve
     * @return InboundTransactionDTO of the retrieved transaction
     * @throws RuntimeException if no transaction is found with the given ID
     */
    public InboundTransactionDTO getInboundTransactionById(Integer id) {
        InboundTransaction transaction = inboundTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inbound transaction not found with ID: " + id));
        return convertToDTO(transaction);
    }

    /**
     * Retrieves all transactions associated with a specific medicine ID.
     * @param medicineId the ID of the medicine
     * @return list of InboundTransactionDTO
     * @throws RuntimeException if no transactions are found for the medicine ID
     */
    public List<InboundTransactionDTO> getTransactionsByMedicineId(Integer medicineId) {
        List<InboundTransaction> transactions = inboundTransactionRepository.findByMedicineId(medicineId);
        if (transactions.isEmpty()) {
            throw new RuntimeException("Inbound transactions not found for medicine ID: " + medicineId);
        }
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converts a transaction entity to its DTO form.
     * @param transaction the entity to convert
     * @return the DTO form of the transaction
     */
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

    /**
     * Converts a DTO to its entity form for persistence.
     * @param dto the DTO to convert
     * @return the entity form of the DTO
     */
    private InboundTransaction convertToEntity(InboundTransactionDTO dto) {
        InboundTransaction transaction = new InboundTransaction();
        Medicine medicine = medicineRepository.findById(dto.getMedicineId()).orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + dto.getMedicineId()));
        transaction.setMedicine(medicine);
        transaction.setQuantity(dto.getQuantity());
        transaction.setSupplier(dto.getSupplier());
        return transaction;
    }

}
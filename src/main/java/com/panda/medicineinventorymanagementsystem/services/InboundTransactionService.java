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
     *
     * @param inboundTransactionRepository repository for accessing inbound transaction data
     * @param medicineRepository           repository for accessing medicine data
     */
    @Autowired
    public InboundTransactionService(InboundTransactionRepository inboundTransactionRepository, MedicineRepository medicineRepository) {
        this.inboundTransactionRepository = inboundTransactionRepository;
        this.medicineRepository = medicineRepository;
    }

    /**
     * Adds a list of inbound transactions and updates the stock quantities for the medicines involved.
     *
     * @param transactionsDTO list of InboundTransactionDTO objects representing the new transactions
     * @return list of InboundTransactionDTO objects after saving to database
     * @throws IllegalStateException if a referenced medicine is not found in the database
     */
    @Transactional
    public List<InboundTransactionDTO> addInboundTransactions(List<InboundTransactionDTO> transactionsDTO) {
        // Extract IDs and check if all medicines exist
        Set<Integer> medicineIds = transactionsDTO.stream()
                .map(InboundTransactionDTO::getMedicineId)
                .collect(Collectors.toSet());
        // Fetch corresponding medicines from the database and store them in a map for quick access
        Map<Integer, Medicine> medicines = medicineRepository.findAllById(medicineIds)
                .stream()
                .collect(Collectors.toMap(Medicine::getId, Function.identity()));


        // Check if all medicines are found, and find the missing id
        Set<Integer> missingMedicines = medicineIds.stream()
                .filter(id -> !medicines.containsKey(id))
                .collect(Collectors.toSet());
        if (!missingMedicines.isEmpty()) {
            throw new IllegalStateException("Medicines not found for IDs: " + missingMedicines);
        }

        // All medicines exist, convert DTOs to entities
        List<InboundTransaction> transactions = transactionsDTO.stream()
                .map(dto -> convertToEntity(dto, medicines))
                .collect(Collectors.toList());

        // Process each transaction to update the medicine quantity and validate the transaction
        transactions.forEach(transaction -> {
            Medicine originalMedicine = transaction.getMedicine();
            int originalQuantity = originalMedicine.getQuantity();
            int addedQuantity = transaction.getQuantity();
            int newQuantity = originalQuantity + addedQuantity;

            // Update the medicine quantity
            originalMedicine.setQuantity(newQuantity);
            transaction.setOriginalMedicineQuantity(originalQuantity);
            transaction.setUpdateTransactionQuantity(newQuantity);
        });

        // Save all updated medicines and transactions
        medicineRepository.saveAll(medicines.values());
        List<InboundTransaction> savedTransactions = inboundTransactionRepository.saveAll(transactions);
        return savedTransactions.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    /**
     * Retrieves all inbound transactions in a paginated format.
     *
     * @param pageable the pagination and sorting information
     * @return Page of InboundTransactionDTO
     */
    public Page<InboundTransactionDTO> getAllInboundTransactions(Pageable pageable) {
        return inboundTransactionRepository.findAll(pageable)
                .map(this::convertToDTO);
    }


    /**
     * Retrieves a single inbound transaction by its ID.
     *
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
     *
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
     *
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
     * Converts an InboundTransactionDTO to an InboundTransaction entity.
     * @param dto The InboundTransactionDTO to convert.
     * @param medicineMap A map of Medicine entities indexed by their IDs, used to fetch the Medicine
     *                    associated with the DTO.
     * @return InboundTransaction The fully constructed InboundTransaction entity, ready for persistence.
     * @throws IllegalStateException If the medicine associated with the DTO's medicineId does not exist
     *                               in the provided medicine map.
     */
    private InboundTransaction convertToEntity(InboundTransactionDTO dto, Map<Integer, Medicine> medicineMap) {
        InboundTransaction transaction = new InboundTransaction();
        Medicine medicine = medicineMap.get(dto.getMedicineId());
        if (medicine == null) {
            throw new IllegalStateException("Medicine not found for ID: " + dto.getMedicineId());
        }
        transaction.setMedicine(medicine);
        transaction.setQuantity(dto.getQuantity());
        transaction.setSupplier(dto.getSupplier());
        return transaction;
    }

}
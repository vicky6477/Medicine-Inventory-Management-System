package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.dto.InboundTransactionDTO;
import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.User;
import com.panda.medicineinventorymanagementsystem.mapper.InboundTransactionMapper;
import com.panda.medicineinventorymanagementsystem.repository.InboundTransactionRepository;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final InboundTransactionMapper inboundTransactionMapper;
    private final UserService userService;

    /**
     * Constructor for InboundTransactionService with dependency injection.
     *
     * @param inboundTransactionRepository repository for accessing inbound transaction data
     * @param medicineRepository           repository for accessing medicine data
     */
    @Autowired
    public InboundTransactionService(InboundTransactionRepository inboundTransactionRepository, MedicineRepository medicineRepository, InboundTransactionMapper inboundTransactionMapper,UserService userService) {
        this.inboundTransactionRepository = inboundTransactionRepository;
        this.medicineRepository = medicineRepository;
        this.inboundTransactionMapper = inboundTransactionMapper;
        this.userService = userService;
    }

    /**
     * Processes and saves a list of inbound transactions by reducing stock quantities.
     *
     * @param transactionsDTO List of inbound transaction data transfer objects.
     * @return List of processed and persisted InboundTransaction entities.
     * @throws EntityNotFoundException if any specified medicine is not found.
     * @throws IllegalStateException if any medicine has insufficient stock.
     */
    @Transactional
    public List<InboundTransaction> addInboundTransactions(List<InboundTransactionDTO> transactionsDTO) {
        Map<Integer, User> userMap = new HashMap<>();
        User currentUser = userService.getCurrentAuthenticatedUser();
        // Extract IDs and check if all medicines exist
        Set<Integer> medicineIds = transactionsDTO.stream()
                .map(InboundTransactionDTO::getMedicineId)
                .collect(Collectors.toSet());

        // Fetch corresponding medicines from the database and store them in a map for quick access
        Map<Integer, Medicine> medicines = medicineRepository.findAllById(medicineIds)
                .stream()
                .collect(Collectors.toMap(Medicine::getId, Function.identity()));

        // Check if all medicines are found, and find the missing id
        Set<Integer> foundIds = medicines.keySet();
        if (medicineIds.size() > foundIds.size()) {
            medicineIds.removeAll(foundIds);
            throw new EntityNotFoundException("Medicines not found for IDs: " + medicineIds);
        }


        // All medicines exist, convert DTOs to entities
        List<InboundTransaction> transactions = transactionsDTO.stream()
                .map(dto -> {
                    Medicine medicine = medicines.get(dto.getMedicineId());
                    if (medicine == null) {
                        throw new EntityNotFoundException("Medicine not found for ID: " + dto.getMedicineId());
                    }
                    InboundTransaction transaction = inboundTransactionMapper.toEntity(dto, medicines,userMap);
                    transaction.setUser(currentUser);
                    return transaction;
                })
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
        return inboundTransactionRepository.saveAll(transactions);
    }


    /**
     * Retrieves all inbound transactions in a paginated format.
     *
     * @param pageable Pagination and sorting information.
     * @return Page containing outbound transactions.
     */
    public Page<InboundTransaction> getAllInboundTransactions(Pageable pageable) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        return inboundTransactionRepository.findAllByUser(currentUser, pageable);
    }


    /**
     * Retrieves a specific inbound transaction by its ID.
     *
     * @param id The ID of the inbound transaction to retrieve.
     * @return The requested InboundTransaction entity.
     * @throws EntityNotFoundException if the transaction is not found.
     */
    public InboundTransaction getInboundTransactionById(Integer id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        return inboundTransactionRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Inbound transaction not found with ID: " + id));
    }


}
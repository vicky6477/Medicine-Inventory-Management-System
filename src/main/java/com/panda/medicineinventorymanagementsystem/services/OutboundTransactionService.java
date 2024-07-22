package com.panda.medicineinventorymanagementsystem.services;
import com.panda.medicineinventorymanagementsystem.dto.OutboundTransactionDTO;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.User;
import com.panda.medicineinventorymanagementsystem.mapper.OutboundTransactionMapper;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import com.panda.medicineinventorymanagementsystem.repository.OutboundTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OutboundTransactionService {
    private final OutboundTransactionRepository outboundTransactionRepository;
    private final MedicineRepository medicineRepository;
    private final OutboundTransactionMapper outboundTransactionMapper;
    private final UserService userService;

    /**
     * Constructor for OutboundTransactionService with dependency injection.
     * @param outboundTransactionRepository Repository for managing outbound transaction data.
     * @param medicineRepository Repository for accessing medicine data.
     */
    @Autowired
    public OutboundTransactionService(OutboundTransactionRepository outboundTransactionRepository, MedicineRepository medicineRepository, OutboundTransactionMapper outboundTransactionMapper, UserService userService) {
        this.outboundTransactionRepository = outboundTransactionRepository;
        this.medicineRepository = medicineRepository;
        this.outboundTransactionMapper = outboundTransactionMapper;
        this.userService = userService;
    }


    /**
     * Processes and saves a list of outbound transactions by reducing stock quantities.
     * Verifies medicine availability and ensures stock levels are sufficient before committing the transaction.
     *
     * @param transactionsDTO List of outbound transaction data transfer objects.
     * @return List of processed and persisted OutboundTransaction entities.
     * @throws EntityNotFoundException if any specified medicine is not found.
     * @throws IllegalStateException if any medicine has insufficient stock.
     */
    @Transactional
    public List<OutboundTransaction> addOutboundTransactions(List<OutboundTransactionDTO> transactionsDTO) {
        Map<Integer, User> userMap = new HashMap<>();
        User currentUser = userService.getCurrentAuthenticatedUser();
        // Extract medicine IDs from DTOs
        Set<Integer> medicineIds = transactionsDTO.stream()
                .map(OutboundTransactionDTO::getMedicineId)
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
        List<OutboundTransaction> transactions = transactionsDTO.stream()
                .map(dto -> {
                    Medicine medicine = medicines.get(dto.getMedicineId());
                    if (medicine == null) {
                        throw new EntityNotFoundException("Medicine not found for ID: " + dto.getMedicineId());
                    }
                    OutboundTransaction transaction = outboundTransactionMapper.toEntity(dto, medicines,userMap);
                    transaction.setUser(currentUser);
                    return transaction;
                })
                .collect(Collectors.toList());

        // Process each transaction to update the medicine quantity and validate the transaction
        transactions.forEach(transaction -> {
            Medicine originalMedicine = transaction.getMedicine();
            int originalQuantity = originalMedicine.getQuantity();
            int subtractedQuantity = transaction.getQuantity();
            int newQuantity = originalQuantity - subtractedQuantity;

            if (newQuantity < 0) {
                throw new IllegalStateException("Insufficient stock for medicine ID: " + originalMedicine.getId());
            }

            // Update medicine quantity
            originalMedicine.setQuantity(newQuantity);
            transaction.setOriginalMedicineQuantity(originalQuantity);
            transaction.setUpdateTransactionQuantity(newQuantity);
        });


        // Save all updated medicines and transactions
        medicineRepository.saveAll(medicines.values());
        return outboundTransactionRepository.saveAll(transactions);
    }


    /**
     * Retrieves all outbound transactions in a paginated format.
     *
     * @param pageable Pagination and sorting information.
     * @return Page containing outbound transactions.
     */
    public Page<OutboundTransaction> getAllOutboundTransactions(Pageable pageable) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        return outboundTransactionRepository.findAllByUser(currentUser, pageable);
    }

    /**
     * Retrieves a specific outbound transaction by its ID.
     *
     * @param id The ID of the outbound transaction to retrieve.
     * @return The requested OutboundTransaction entity.
     * @throws EntityNotFoundException if the transaction is not found.
     */
    public OutboundTransaction getOutboundTransactionById(Integer id) {
        User currentUser = userService.getCurrentAuthenticatedUser();
        return outboundTransactionRepository.findByIdAndUser(id,currentUser)
                .orElseThrow(() -> new EntityNotFoundException("Outbound transaction not found with ID: " + id));
    }

}

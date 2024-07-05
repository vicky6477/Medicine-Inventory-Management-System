package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.dto.OutboundTransactionDTO;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import com.panda.medicineinventorymanagementsystem.repository.OutboundTransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    @Autowired
    private EntityManager entityManager;

    //Create medicines outbound transactions
    @Transactional
    public List<OutboundTransactionDTO> addOutboundTransactions(List<OutboundTransactionDTO> transactionsDTO) {
        List<OutboundTransaction> transactions = transactionsDTO.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());

        // Collect all medicine IDs from the transactions for batch fetching
        Set<Integer> medicineIds = transactions.stream()
                .map(tx -> tx.getMedicine().getId())
                .collect(Collectors.toSet());

        // Fetch all corresponding medicines once and store them in a map for quick access
        Map<Integer, Medicine> medicines = medicineRepository.findAllById(medicineIds)
                .stream()
                .collect(Collectors.toMap(Medicine::getId, Function.identity()));

        // Process each transaction to update the medicine quantity and validate the transaction
        transactions.forEach(transaction -> {
            Medicine originalMedicine = medicines.get(transaction.getMedicine().getId());
            if (originalMedicine == null) {
                throw new IllegalStateException("Medicine with ID " + transaction.getMedicine().getId() + " not found");
            }
            transaction.setOriginalMedicineQuantity(originalMedicine.getQuantity());
            int newQuantity = originalMedicine.getQuantity() - transaction.getQuantity();
            if (newQuantity < 0) {
                throw new IllegalStateException("Insufficient stock for medicine ID " + originalMedicine.getId());
            }
            originalMedicine.setQuantity(newQuantity); // Update the medicine's quantity
            transaction.setMedicine(originalMedicine); // Set the updated originalMedicine object in the transaction
            transaction.setUpdateTransactionQuantity(newQuantity);
        });

        // Save all modified medicines to the database in a batch operation
        medicineRepository.saveAll(medicines.values());

        // Save all transactions to the database in a batch operation and return them
        List<OutboundTransaction> savedTransactions = outboundTransactionRepository.saveAll(transactions);
        return savedTransactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    //Retrieve all outbound transactions
    public Page<OutboundTransactionDTO> getAllOutboundTransactions(Pageable pageable) {
        return outboundTransactionRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    //Retrieve a single transaction by id
    public OutboundTransactionDTO getOutboundTransactionById(Integer id) {
        OutboundTransaction transaction = outboundTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Outbound transaction not found with ID: " + id));
        return convertToDTO(transaction);
    }

    // Retrieve transactions by medicineId
    public List<OutboundTransactionDTO> getTransactionsByMedicineId(Integer medicineId) {
        List<OutboundTransaction> transactions = outboundTransactionRepository.findByMedicineId(medicineId);
        if (transactions.isEmpty()) {
            throw new RuntimeException("Outbound transactions not found for medicine ID: " + medicineId);
        }
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

/*    @Transactional
    public OutboundTransaction updateOutboundTransaction(Integer id, OutboundTransaction updatedTransaction) {
        OutboundTransaction transaction = getOutboundTransactionById(id);
        transaction.setQuantity(updatedTransaction.getQuantity());
        transaction.setMedicine(updatedTransaction.getMedicine());
        return outboundTransactionRepository.save(transaction);
    }

    @Transactional
    public void deleteOutboundTransaction(Integer id) {
        OutboundTransaction transaction = getOutboundTransactionById(id);
        outboundTransactionRepository.delete(transaction);
    }*/

    private OutboundTransactionDTO convertToDTO(OutboundTransaction transaction) {
        OutboundTransactionDTO dto = new OutboundTransactionDTO();
        dto.setId(transaction.getId());
        dto.setMedicineId(transaction.getMedicine().getId());
        dto.setQuantity(transaction.getQuantity());
        dto.setOriginalMedicineQuantity(transaction.getOriginalMedicineQuantity());
        dto.setUpdateTransactionQuantity(transaction.getUpdateTransactionQuantity());
        dto.setDispatcheddDate(transaction.getDispatcheddDate());
        dto.setSupplier(transaction.getSupplier());
        return dto;
    }

    private OutboundTransaction convertToEntity(OutboundTransactionDTO dto) {
        OutboundTransaction transaction = new OutboundTransaction();
        Medicine medicine = medicineRepository.findById(dto.getMedicineId()).orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + dto.getMedicineId()));
        transaction.setMedicine(medicine);
        transaction.setQuantity(dto.getQuantity());
        transaction.setSupplier(dto.getSupplier());
        return transaction;
    }
}

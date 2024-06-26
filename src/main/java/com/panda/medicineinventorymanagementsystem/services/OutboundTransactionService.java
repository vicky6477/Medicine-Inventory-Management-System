package com.panda.medicineinventorymanagementsystem.services;

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

import java.util.ArrayList;
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
    public List<OutboundTransaction> addOutboundTransactions(List<OutboundTransaction> transactions) {
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
        return outboundTransactionRepository.saveAll(transactions);
    }

    /*@Transactional
    public List<OutboundTransaction> addOutboundTransactions(List<OutboundTransaction> transactions) {
        // Collect all medicine IDs from the transactions
        Set<Integer> medicineIds = transactions.stream()
                .map(transaction -> transaction.getMedicine().getId())
                .collect(Collectors.toSet());

        // Fetch all medicines once and store them in a map for quick lookup
        Map<Integer, Medicine> medicines = medicineRepository.findAllById(medicineIds)
                .stream()
                .collect(Collectors.toMap(Medicine::getId, Function.identity()));

        List<Medicine> batchMedicines = new ArrayList<>();

        // Update the quantities and set the original Medicine object into each transaction
        for (int i = 0; i < transactions.size(); i++) {
            OutboundTransaction transaction = transactions.get(i);
            Medicine originalMedicine = medicines.get(transaction.getMedicine().getId());
            if (originalMedicine == null) {
                throw new IllegalStateException("Medicine with ID " + transaction.getMedicine().getId() + " not found");
            }
            int newQuantity = originalMedicine.getQuantity() - transaction.getQuantity();
            if (newQuantity < 0) {
                throw new IllegalStateException("Insufficient stock for medicine ID " + originalMedicine.getId());
            }

            // Set the original quantity for the transaction before modifying the medicine's quantity
            transaction.setOriginalMedicineQuantity(originalMedicine.getQuantity());
            originalMedicine.setQuantity(newQuantity);

            // Update the medicine in the transaction after modifying its quantity
            transaction.setMedicine(originalMedicine);
            transaction.setUpdateTransactionQuantity(newQuantity);

            batchMedicines.add(originalMedicine);

            if ((i + 1) % 100 == 0 || i == transactions.size() - 1) {
                medicineRepository.saveAll(batchMedicines);
                entityManager.flush();
                batchMedicines.clear();
            }
        }

        // Ensure all transactions have their required fields set before saving
        return outboundTransactionRepository.saveAll(transactions);
    }
*/


    //Retrieve all outbound transactions
    public Page<OutboundTransaction> getAllOutboundTransactions(Pageable pageable) {
        return outboundTransactionRepository.findAll(pageable);
    }

    //in real world situation, we'll have order number, so we could use it to modify the transaction(using id here for now)
    public OutboundTransaction getOutboundTransactionById(Integer id) {
        return outboundTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Outbound transaction not found with ID: " + id));
    }

    @Transactional
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
    }
}

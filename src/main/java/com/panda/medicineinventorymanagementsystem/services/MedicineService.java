package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import com.panda.medicineinventorymanagementsystem.repository.InboundTransactionRepository;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import com.panda.medicineinventorymanagementsystem.repository.OutboundTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicineService {
    private final MedicineRepository medicineRepository;
    private final InboundTransactionRepository inboundTransactionRepository;
    private final OutboundTransactionRepository outboundTransactionRepository;

    //Constructor injection
    @Autowired
    public MedicineService(MedicineRepository medicineRepository, InboundTransactionRepository inboundTransactionRepository, OutboundTransactionRepository outboundTransactionRepository) {
        this.medicineRepository = medicineRepository;
        this.inboundTransactionRepository = inboundTransactionRepository;
        this.outboundTransactionRepository = outboundTransactionRepository;
    }
    //Create medicine
    public Medicine createMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    //Retrieve a medicine by its ID.
    public Medicine getMedicineById(Integer id) {
        return medicineRepository.findById(id).orElseThrow(() -> new RuntimeException("id not found"));
    }

    //Retrieve all medicines, with the ability to paginate the results.
    public Page<Medicine> findAllMedicines(Pageable pageable) {
        return medicineRepository.findAll(pageable);
    }

    //Updates the details of an existing medicine
    public Medicine updateMedicine(Integer id, Medicine medicineDetails) {
        //Retrieve the medicine by its id, if medicine does not exit, throw exception
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
        medicine.setName(medicineDetails.getName());
        medicine.setDescription(medicineDetails.getDescription());
        medicine.setQuantity(medicineDetails.getQuantity());
        medicine.setType(medicineDetails.getType());
        return medicineRepository.save(medicine);
    }


    //Create medicines inbound transactions
    @Transactional
    public List<Medicine> addInboundTransactions(List<InboundTransaction> transactions) {
        transactions.forEach(transaction -> {
            Medicine medicine = medicineRepository.findById(transaction.getMedicine().getId())
                    .orElseThrow(() -> new RuntimeException("Medicine not found"));
            medicine.setQuantity(medicine.getQuantity() + transaction.getQuantity());
            inboundTransactionRepository.save(transaction);
            medicineRepository.save(medicine);
        });
        return medicineRepository.findAllById(transactions.stream().map(transaction -> transaction.getMedicine().getId()).collect(Collectors.toList()));
    }

    //Create medicines outbound transactions
    @Transactional
    public List<Medicine> addOutboundTransactions(List<OutboundTransaction> transactions) {
        transactions.forEach(transaction -> {
            Medicine medicine = medicineRepository.findById(transaction.getMedicine().getId())
                    .orElseThrow(() -> new RuntimeException("Medicine not found"));
            int newQuantity = medicine.getQuantity() - transaction.getQuantity();
            if (newQuantity < 0) {
                throw new IllegalStateException("Insufficient stock for medicine ID " + medicine.getId());
            }
            medicine.setQuantity(newQuantity);
            outboundTransactionRepository.save(transaction);
            medicineRepository.save(medicine);
        });
        return medicineRepository.findAllById(transactions.stream().map(transaction -> transaction.getMedicine().getId()).collect(Collectors.toList()));
    }

    //Retrieve all inbound transactions
    public Page<InboundTransaction> getAllInboundTransactions(Pageable pageable) {
        return inboundTransactionRepository.findAll(pageable);
    }

    ////Retrieve all outbound transactions
    public Page<OutboundTransaction> getAllOutboundTransactions(Pageable pageable) {
        return outboundTransactionRepository.findAll(pageable);
    }
}

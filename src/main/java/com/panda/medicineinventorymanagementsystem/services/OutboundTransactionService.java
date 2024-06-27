//package com.panda.medicineinventorymanagementsystem.services;
//
//import com.panda.medicineinventorymanagementsystem.entity.Medicine;
//import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
//import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
//import com.panda.medicineinventorymanagementsystem.repository.OutboundTransactionRepository;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class OutboundTransactionService {
//    private final OutboundTransactionRepository outboundTransactionRepository;
//    private final MedicineRepository medicineRepository;
//
//    //Construct injection
//    @Autowired
//    public OutboundTransactionService(OutboundTransactionRepository outboundTransactionRepository, MedicineRepository medicineRepository) {
//        this.outboundTransactionRepository = outboundTransactionRepository;
//        this.medicineRepository = medicineRepository;
//    }
//
//    ///Create medicines outbound transactions
//    @Transactional
//    public List<Medicine> addOutboundTransactions(List<OutboundTransaction> transactions) {
//        transactions.forEach(transaction -> {
//            Medicine medicine = medicineRepository.findById(transaction.getMedicine().getId())
//                    .orElseThrow(() -> new RuntimeException("Medicine not found"));
//            int newQuantity = medicine.getQuantity() - transaction.getQuantity();
//            if (newQuantity < 0) {
//                throw new IllegalStateException("Insufficient stock for medicine ID " + medicine.getId());
//            }
//            medicine.setQuantity(newQuantity);
//            outboundTransactionRepository.save(transaction);
//            medicineRepository.save(medicine);
//        });
//        return medicineRepository.findAllById(transactions.stream().map(transaction -> transaction.getMedicine().getId()).collect(Collectors.toList()));
//    }
//
//    ////Retrieve all outbound transactions
//    public Page<OutboundTransaction> getAllOutboundTransactions(Pageable pageable) {
//        return outboundTransactionRepository.findAll(pageable);
//    }
//
//}

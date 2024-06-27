//package com.panda.medicineinventorymanagementsystem.services;
//
//import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
//import com.panda.medicineinventorymanagementsystem.entity.Medicine;
//import com.panda.medicineinventorymanagementsystem.repository.InboundTransactionRepository;
//import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
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
//public class InboundTransactionService {
//    private final InboundTransactionRepository inboundTransactionRepository;
//    private final MedicineRepository medicineRepository;
//
//    //Construct injection
//    @Autowired
//    public InboundTransactionService(InboundTransactionRepository inboundTransactionRepository, MedicineRepository medicineRepository) {
//        this.inboundTransactionRepository = inboundTransactionRepository;
//        this.medicineRepository = medicineRepository;
//    }
//
//    //Create medicines transactions
//    //Create medicines inbound transactions
//    @Transactional
//    public List<Medicine> addInboundTransactions(List<InboundTransaction> transactions) {
//        transactions.forEach(transaction -> {
//            Medicine medicine = medicineRepository.findById(transaction.getMedicine().getId())
//                    .orElseThrow(() -> new RuntimeException("Medicine not found"));
//            medicine.setQuantity(medicine.getQuantity() + transaction.getQuantity());
//            inboundTransactionRepository.save(transaction);
//            medicineRepository.save(medicine);
//        });
//        return medicineRepository.findAllById(transactions.stream().map(transaction -> transaction.getMedicine().getId()).collect(Collectors.toList()));
//    }
//    //Retrieve all transactions
//    public Page<InboundTransaction> getAllInboundTransactions(Pageable pageable) {
//        return inboundTransactionRepository.findAll(pageable);
//    }
//
//}

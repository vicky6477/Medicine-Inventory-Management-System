//package com.panda.medicineinventorymanagementsystem.controller;
//
//import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
//import com.panda.medicineinventorymanagementsystem.entity.Medicine;
//import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
//import com.panda.medicineinventorymanagementsystem.services.InboundTransactionService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/inbound/transactions")
//public class InboundTransactionController {
//    private final InboundTransactionService inboundTransactionService;
//
//    @Autowired
//    public InboundTransactionController(InboundTransactionService inboundTransactionService) {
//        this.inboundTransactionService = inboundTransactionService;
//    }
//
//    @PostMapping
//    public ResponseEntity<List<Medicine>> addInboundTransactions(@RequestBody List<InboundTransaction> transactions) {
//        return ResponseEntity.ok(inboundTransactionService.addInboundTransactions(transactions));
//    }
//
//    @GetMapping
//    public ResponseEntity<Page<InboundTransaction>> getAllInboundTransactions(Pageable pageable) {
//        return ResponseEntity.ok(inboundTransactionService.getAllInboundTransactions(pageable));
//    }
//
//}

//package com.panda.medicineinventorymanagementsystem.controller;
//import com.panda.medicineinventorymanagementsystem.entity.Medicine;
//import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
//import com.panda.medicineinventorymanagementsystem.services.OutboundTransactionService;
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
//@RequestMapping("/outbound/transactions")
//public class OutboundTransactionController {
//    private final OutboundTransactionService outboundTransactionService;
//
//    @Autowired
//    public OutboundTransactionController(OutboundTransactionService outboundTransactionService) {
//        this.outboundTransactionService = outboundTransactionService;
//    }
//
//    @PostMapping
//    public ResponseEntity<List<Medicine>> addOutboundTransactions(@RequestBody List<OutboundTransaction> transactions) {
//        return ResponseEntity.ok(outboundTransactionService.addOutboundTransactions(transactions));
//    }
//
//    @GetMapping
//    public ResponseEntity<Page<OutboundTransaction>> getAllOutboundTransactions(Pageable pageable) {
//        return ResponseEntity.ok(outboundTransactionService.getAllOutboundTransactions(pageable));
//    }
//
//}

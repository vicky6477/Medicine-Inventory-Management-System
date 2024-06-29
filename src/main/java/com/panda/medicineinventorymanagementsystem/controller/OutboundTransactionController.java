package com.panda.medicineinventorymanagementsystem.controller;

import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import com.panda.medicineinventorymanagementsystem.services.OutboundTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/outbound/transactions")
public class OutboundTransactionController {
    private final OutboundTransactionService outboundTransactionService;

    @Autowired
    public OutboundTransactionController(OutboundTransactionService outboundTransactionService) {
        this.outboundTransactionService = outboundTransactionService;
    }

   /* @PostMapping
    public ResponseEntity<List<Medicine>> addOutboundTransactions(@RequestBody List<OutboundTransaction> transactions) {
        return ResponseEntity.ok(outboundTransactionService.addOutboundTransactions(transactions));
    }*/

   /* @PostMapping
    public ResponseEntity<List<OutboundTransaction>> createOutboundTransactions(@RequestBody List<OutboundTransaction> transactions) {
        try {
            List<OutboundTransaction> savedTransactions = outboundTransactionService.addOutboundTransactions(transactions);
            return ResponseEntity.ok(savedTransactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }*/

    @PostMapping
    public ResponseEntity<?> createOutboundTransactions(@RequestBody List<OutboundTransaction> transactions) {
        try {
            List<OutboundTransaction> savedTransactions = outboundTransactionService.addOutboundTransactions(transactions);
            return ResponseEntity.ok(savedTransactions);
        } catch (IllegalStateException e) {
            // 返回具体的业务规则违反错误信息
            return ResponseEntity.badRequest().body("Business rule violation: " + e.getMessage());
        } catch (Exception e) {
            // 提供更多的错误信息方便调试
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<OutboundTransaction>> getAllOutboundTransactions(Pageable pageable) {
        return ResponseEntity.ok(outboundTransactionService.getAllOutboundTransactions(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OutboundTransaction> getOutboundTransactionById(@PathVariable Integer id) {
        try {
            OutboundTransaction transaction = outboundTransactionService.getOutboundTransactionById(id);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OutboundTransaction> updateOutboundTransaction(@PathVariable Integer id, @RequestBody OutboundTransaction transaction) {
        try {
            OutboundTransaction updatedTransaction = outboundTransactionService.updateOutboundTransaction(id, transaction);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOutboundTransaction(@PathVariable Integer id) {
        try {
            outboundTransactionService.deleteOutboundTransaction(id);
            return ResponseEntity.ok("Transaction deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found with ID: " + id);
        }
    }
}
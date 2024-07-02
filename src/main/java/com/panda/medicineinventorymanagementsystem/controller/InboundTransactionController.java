package com.panda.medicineinventorymanagementsystem.controller;

import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import com.panda.medicineinventorymanagementsystem.services.InboundTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inbound/transactions")
public class InboundTransactionController {
    private final InboundTransactionService inboundTransactionService;

    @Autowired
    public InboundTransactionController(InboundTransactionService inboundTransactionService) {
        this.inboundTransactionService = inboundTransactionService;
    }


    @PostMapping
    public ResponseEntity<List<InboundTransaction>> createInboundTransactions(@RequestBody List<InboundTransaction> transactions) {
        try {
            List<InboundTransaction> savedTransactions = inboundTransactionService.addInboundTransactions(transactions);
            return ResponseEntity.ok(savedTransactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<InboundTransaction>> getAllInboundTransactions(Pageable pageable) {
        return ResponseEntity.ok(inboundTransactionService.getAllInboundTransactions(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InboundTransaction> getInboundTransactionById(@PathVariable Integer id) {
        try {
            InboundTransaction transaction = inboundTransactionService.getInboundTransactionById(id);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT to update a transaction
    @PutMapping("/{id}")
    public ResponseEntity<InboundTransaction> updateInboundTransaction(@PathVariable Integer id, @RequestBody InboundTransaction transaction) {
        try {
            InboundTransaction updatedTransaction = inboundTransactionService.updateInboundTransaction(id, transaction);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE a transaction
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInboundTransaction(@PathVariable Integer id) {
        try {
            inboundTransactionService.deleteInboundTransaction(id);
            return ResponseEntity.ok("Transaction deleted successfully.");
        } catch (RuntimeException e) {
            // Assuming RuntimeException is thrown when the transaction cannot be found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found with ID: " + id);
        }
    }

}
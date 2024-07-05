package com.panda.medicineinventorymanagementsystem.controller;

import com.panda.medicineinventorymanagementsystem.dto.OutboundTransactionDTO;
import com.panda.medicineinventorymanagementsystem.services.OutboundTransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/outbound/transactions")
public class OutboundTransactionController {
    private final OutboundTransactionService outboundTransactionService;

    @Autowired
    public OutboundTransactionController(OutboundTransactionService outboundTransactionService) {
        this.outboundTransactionService = outboundTransactionService;
    }


    @PostMapping
    public ResponseEntity<?> createOutboundTransactions(@Valid @RequestBody List<OutboundTransactionDTO> transactionsDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            List<OutboundTransactionDTO> savedTransactions = outboundTransactionService.addOutboundTransactions(transactionsDTO);
            return ResponseEntity.ok(savedTransactions);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<OutboundTransactionDTO>> getAllOutboundTransactions(Pageable pageable) {
        return ResponseEntity.ok(outboundTransactionService.getAllOutboundTransactions(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOutboundTransactionById(@PathVariable Integer id) {
        try {
            OutboundTransactionDTO transactionDTO = outboundTransactionService.getOutboundTransactionById(id);
            return ResponseEntity.ok(transactionDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/by-medicine/{medicineId}")
    public ResponseEntity<?> getTransactionsByMedicineId(@PathVariable Integer medicineId) {
        try {
            List<OutboundTransactionDTO> transactions = outboundTransactionService.getTransactionsByMedicineId(medicineId);
            return ResponseEntity.ok(transactions);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

 /*   @PutMapping("/{id}")
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
    }*/
}
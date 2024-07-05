package com.panda.medicineinventorymanagementsystem.controller;

import com.panda.medicineinventorymanagementsystem.dto.InboundTransactionDTO;
import com.panda.medicineinventorymanagementsystem.services.InboundTransactionService;
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
@RequestMapping("/inbound/transactions")
public class InboundTransactionController {
    private final InboundTransactionService inboundTransactionService;

    @Autowired
    public InboundTransactionController(InboundTransactionService inboundTransactionService) {
        this.inboundTransactionService = inboundTransactionService;
    }


    @PostMapping
    public ResponseEntity<?> createInboundTransactions(@Valid @RequestBody List<InboundTransactionDTO> transactionsDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            List<InboundTransactionDTO> savedTransactions = inboundTransactionService.addInboundTransactions(transactionsDTO);
            return ResponseEntity.ok(savedTransactions);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<InboundTransactionDTO>> getAllInboundTransactions(Pageable pageable) {
        return ResponseEntity.ok(inboundTransactionService.getAllInboundTransactions(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInboundTransactionById(@PathVariable Integer id) {
        try {
            InboundTransactionDTO transactionDTO = inboundTransactionService.getInboundTransactionById(id);
            return ResponseEntity.ok(transactionDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/by-medicine/{medicineId}")
    public ResponseEntity<?> getTransactionsByMedicineId(@PathVariable Integer medicineId) {
        try {
            List<InboundTransactionDTO> transactions = inboundTransactionService.getTransactionsByMedicineId(medicineId);
            return ResponseEntity.ok(transactions);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

   /* // PUT to update a transaction
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInboundTransaction(@PathVariable Integer id,@Valid @RequestBody InboundTransactionDTO transactionDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }
        try {
            InboundTransactionDTO updatedTransactionDTO = inboundTransactionService.updateInboundTransaction(id, transactionDTO);
            return ResponseEntity.ok(updatedTransactionDTO);
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
    }*/

}
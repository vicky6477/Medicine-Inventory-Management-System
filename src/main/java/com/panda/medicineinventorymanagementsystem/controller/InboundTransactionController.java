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
@RequestMapping("/inboundTransaction")
public class InboundTransactionController {
    private final InboundTransactionService inboundTransactionService;

    @Autowired
    public InboundTransactionController(InboundTransactionService inboundTransactionService) {
        this.inboundTransactionService = inboundTransactionService;
    }

    // POST endpoint to add a list of outbound transactions
    @PostMapping
    public ResponseEntity<List<InboundTransaction>> addInboundTransactions(@RequestBody List<InboundTransaction> transactions) {
        try {
            List<InboundTransaction> savedTransactions = inboundTransactionService.addInboundTransactions(transactions);
            return new ResponseEntity<>(savedTransactions, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // GET endpoint to retrieve all outbound transactions with pagination
    @GetMapping
    public ResponseEntity<Page<InboundTransaction>> getAllOutboundTransactions(Pageable pageable) {
        Page<InboundTransaction> transactions = inboundTransactionService.getAllInboundTransactions(pageable);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

}

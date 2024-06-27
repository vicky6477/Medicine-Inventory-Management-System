package com.panda.medicineinventorymanagementsystem.controller;
import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import com.panda.medicineinventorymanagementsystem.services.OutboundTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/outboundTransaction")
public class OutboundTransactionController {
    private final OutboundTransactionService outboundTransactionService;

    @Autowired
    public OutboundTransactionController(OutboundTransactionService outboundTransactionService) {
        this.outboundTransactionService = outboundTransactionService;
    }

    // POST endpoint to add a list of outbound transactions
    @PostMapping
    public ResponseEntity<List<OutboundTransaction>> addOutboundTransactions(@RequestBody List<OutboundTransaction> transactions) {
        try {
            List<OutboundTransaction> savedTransactions = outboundTransactionService.addOutboundTransactions(transactions);
            return new ResponseEntity<>(savedTransactions, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // GET endpoint to retrieve all outbound transactions with pagination
    @GetMapping
    public ResponseEntity<Page<OutboundTransaction>> getAllOutboundTransactions(Pageable pageable) {
        Page<OutboundTransaction> transactions = outboundTransactionService.getAllOutboundTransactions(pageable);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

}

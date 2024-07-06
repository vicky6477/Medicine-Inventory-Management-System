package com.panda.medicineinventorymanagementsystem.controller;

import com.panda.medicineinventorymanagementsystem.dto.InboundTransactionDTO;
import com.panda.medicineinventorymanagementsystem.services.InboundTransactionService;
import com.panda.medicineinventorymanagementsystem.util.ControllerHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inbound/transactions")
public class InboundTransactionController {
    private final InboundTransactionService inboundTransactionService;

    @Autowired
    public InboundTransactionController(InboundTransactionService inboundTransactionService) {
        this.inboundTransactionService = inboundTransactionService;
    }


    @PostMapping
    @Operation(summary = "Add new inbound transactions", description = "Create new inbound transactions in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InboundTransactionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid transaction data", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Missing Supplier Name", value = "{\"errors\": [\"supplier: Supplier name is required\"]}"),
                    @ExampleObject(name = "Invalid Quantity", value = "{\"errors\": [\"quantity: Quantity must be at least 1\"]}"),
                    @ExampleObject(name = "Missing Medicine ID", value = "{\"errors\": [\"medicineId: Medicine ID cannot be null\"]}")
            })),
            @ApiResponse(responseCode = "404", description = "Medicine not found", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Medicine Not Found", value = "{\"error\": \"Medicine with ID [id] not found\"}")
            })),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> createInboundTransactions(@Valid @RequestBody List<InboundTransactionDTO> transactionsDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = ControllerHelper.formatBindingErrors(bindingResult);
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            List<InboundTransactionDTO> savedTransactions = inboundTransactionService.addInboundTransactions(transactionsDTO);
            return ResponseEntity.ok(savedTransactions);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "List all inbound transactions", description = "Retrieve a paginated list of all inbound transactions in the system. The 'sort' parameter should be a field name from InboundTransactionDTO such as 'medicineId', 'quantity', or 'supplier'. It supports ascending or descending order, e.g., 'quantity,asc' or 'quantity,desc'.")
    @ApiResponse(responseCode = "200", description = "Retrieve transactions successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request. If sort parameters are incorrect or not present in InboundTransactionDTO.", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "Sort Parameter Error", value = "{\"error\": \"Invalid sort parameter: The sort parameter must be a valid field of InboundTransactionDTO.\"}")
    }))

    public ResponseEntity<Page<InboundTransactionDTO>> getAllInboundTransactions(Pageable pageable) {
        return ResponseEntity.ok(inboundTransactionService.getAllInboundTransactions(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific inbound transaction", description = "Retrieve a specific inbound transaction by its ID.")
    @ApiResponse(responseCode = "200", description = "Transaction found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InboundTransactionDTO.class)))
    @ApiResponse(responseCode = "404", description = "Transaction not found ", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "Medicine Not Found", value = "{\"error\": \"Transaction with ID [id] not found\"}")
    }))
    public ResponseEntity<?> getInboundTransactionById(@PathVariable Integer id) {
        try {
            InboundTransactionDTO transactionDTO = inboundTransactionService.getInboundTransactionById(id);
            return ResponseEntity.ok(transactionDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/by-medicine/{medicineId}")
    @Operation(summary = "Get transactions by medicine ID", description = "Retrieve all transactions related to a specific medicine.")
    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @ApiResponse(responseCode = "404", description = "No transactions found for the medicine ID", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "Medicine Not Found", value = "{\"error\": \"Medicine with ID [id] not found\"}")
    }))
    public ResponseEntity<?> getTransactionsByMedicineId(@PathVariable Integer medicineId) {
        try {
            List<InboundTransactionDTO> transactions = inboundTransactionService.getTransactionsByMedicineId(medicineId);
            return ResponseEntity.ok(transactions);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
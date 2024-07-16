package com.panda.medicineinventorymanagementsystem.controller;
import com.panda.medicineinventorymanagementsystem.dto.OutboundTransactionDTO;
import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import com.panda.medicineinventorymanagementsystem.mapper.OutboundTransactionMapper;
import com.panda.medicineinventorymanagementsystem.services.OutboundTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/outbound/transactions")
@Slf4j
public class OutboundTransactionController {
    private final OutboundTransactionService outboundTransactionService;
    private final OutboundTransactionMapper outboundTransactionMapper;

    @Autowired
    public OutboundTransactionController(OutboundTransactionService outboundTransactionService, OutboundTransactionMapper outboundTransactionMapper) {
        this.outboundTransactionService = outboundTransactionService;
        this.outboundTransactionMapper = outboundTransactionMapper;


    }

    @PostMapping
    @Operation(summary = "Add new outbound transactions", description = "Create new outbound transactions in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InboundTransaction.class))),
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
    public ResponseEntity<List<OutboundTransactionDTO>> createOutboundTransactions(@Valid @RequestBody List<OutboundTransactionDTO> transactionsDTO){
        List<OutboundTransaction> transactions = outboundTransactionService.addOutboundTransactions(transactionsDTO);
        List<OutboundTransactionDTO> transactionDTOs = transactions.stream()
                .map(outboundTransactionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }


    @GetMapping
    @Operation(summary = "List all inbound transactions", description = "Retrieve a paginated list of all inbound transactions in the system. The 'sort' parameter should be a field name from InboundTransactionDTO such as 'medicineId', 'quantity', or 'supplier'. It supports ascending or descending order, e.g., 'quantity,asc' or 'quantity,desc'.")
    @ApiResponse(responseCode = "200", description = "Retrieve transactions successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request. If sort parameters are incorrect or not present in OutboundTransactionDTO.", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "Sort Parameter Error", value = "{\"error\": \"Invalid sort parameter: The sort parameter must be a valid field of InboundTransactionDTO.\"}")
    }))
    public ResponseEntity<Page<OutboundTransaction>> getAllOutboundTransactions(Pageable pageable) {
        Page<OutboundTransaction> page = outboundTransactionService.getAllOutboundTransactions(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific inbound transaction", description = "Retrieve a specific inbound transaction by its ID.")
    @ApiResponse(responseCode = "200", description = "Transaction found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InboundTransaction.class)))
    @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "Medicine Not Found", value = "{\"error\": \"Transaction with ID [id] not found\"}")
    }))
    public ResponseEntity<OutboundTransaction> getOutboundTransactionById(@PathVariable Integer id) {
        OutboundTransaction transaction = outboundTransactionService.getOutboundTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/by-medicine/{medicineId}")
    @Operation(summary = "Get transactions by medicine ID", description = "Retrieve all transactions related to a specific medicine.")
    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    @ApiResponse(responseCode = "404", description = "No transactions found for the medicine ID", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "Medicine Not Found", value = "{\"error\": \"Medicine with ID [id] not found\"}")
    }))
    public ResponseEntity<List<OutboundTransaction>> getTransactionsByMedicineId(@PathVariable Integer medicineId) {
        List<OutboundTransaction> transactions = outboundTransactionService.getTransactionsByMedicineId(medicineId);
        return ResponseEntity.ok(transactions);
    }

}
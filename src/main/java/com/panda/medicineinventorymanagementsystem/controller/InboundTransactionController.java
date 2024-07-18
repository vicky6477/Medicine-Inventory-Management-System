package com.panda.medicineinventorymanagementsystem.controller;

import com.panda.medicineinventorymanagementsystem.dto.InboundTransactionDTO;
import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import com.panda.medicineinventorymanagementsystem.mapper.InboundTransactionMapper;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inbound/transactions")
public class InboundTransactionController {
    private final InboundTransactionService inboundTransactionService;
    private final InboundTransactionMapper inboundTransactionMapper;

    @Autowired
    public InboundTransactionController(InboundTransactionService inboundTransactionService, InboundTransactionMapper inboundTransactionMapper) {
        this.inboundTransactionService = inboundTransactionService;
        this.inboundTransactionMapper = inboundTransactionMapper;
    }

    @PostMapping
    @Operation(summary = "Add new inbound transactions", description = "Create new inbound transactions in the database.")
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
    public ResponseEntity<List<InboundTransactionDTO>> createInboundTransactions(@Valid @RequestBody List<InboundTransactionDTO> transactionsDTO) {
        List<InboundTransaction> transactions = inboundTransactionService.addInboundTransactions(transactionsDTO);
        List<InboundTransactionDTO> transactionDTOs = transactions.stream()
                .map(inboundTransactionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }
    public ResponseEntity<?> createInboundTransactions(@Valid @RequestBody List<InboundTransactionDTO> transactionsDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = ControllerHelper.formatBindingErrors(bindingResult);
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            List<InboundTransaction> transactions = inboundTransactionService.addInboundTransactions(transactionsDTO);
            List<InboundTransactionDTO> transactionDTOs = transactions.stream()
                    .map(inboundTransactionMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(transactionDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping
    @Operation(summary = "List all inbound transactions", description = "Retrieve a paginated list of all inbound transactions in the system.")
    @ApiResponse(responseCode = "200", description = "Retrieve transactions successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request. If sort parameters are incorrect or not present in InboundTransactionDTO.", content = @Content(mediaType = "application/json", examples = {
            @ExampleObject(name = "Sort Parameter Error", value = "{\"error\": \"Invalid sort parameter: The sort parameter must be a valid field of InboundTransactionDTO.\"}")
    }))
    public ResponseEntity<Page<InboundTransactionDTO>> getAllInboundTransactions(Pageable pageable) {
        Page<InboundTransaction> page = inboundTransactionService.getAllInboundTransactions(pageable);
        Page<InboundTransactionDTO> dtoPage = page.map(inboundTransactionMapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific inbound transaction", description = "Retrieve a specific inbound transaction by its ID.")
    public ResponseEntity<InboundTransactionDTO> getInboundTransactionById(@PathVariable Integer id) {
        InboundTransaction transaction = inboundTransactionService.getInboundTransactionById(id);
        InboundTransactionDTO dto = inboundTransactionMapper.toDTO(transaction);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/by-medicine/{medicineId}")
    @Operation(summary = "Get transactions by medicine ID", description = "Retrieve all transactions related to a specific medicine.")
    public ResponseEntity<List<InboundTransactionDTO>> getTransactionsByMedicineId(@PathVariable Integer medicineId) {
        List<InboundTransaction> transactions = inboundTransactionService.getTransactionsByMedicineId(medicineId);
        List<InboundTransactionDTO> transactionDTOs = transactions.stream()
                .map(inboundTransactionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }
}

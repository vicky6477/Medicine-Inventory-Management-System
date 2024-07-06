package com.panda.medicineinventorymanagementsystem.controller;

import com.panda.medicineinventorymanagementsystem.dto.MedicineDTO;
import com.panda.medicineinventorymanagementsystem.services.MedicineService;
import com.panda.medicineinventorymanagementsystem.util.ControllerHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;

import java.util.Map;


@RestController
@RequestMapping("/medicines")
public class MedicineController {
    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @PostMapping
    @Operation(summary = "Create a new medicine", description = "Adds a new medicine to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medicine created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicineDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error: type must be one of the predefined types.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate medicine name.", content = @Content()),
    })
    public ResponseEntity<?> createMedicine(@Valid @RequestBody MedicineDTO medicineDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = ControllerHelper.formatBindingErrors(bindingResult);
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            MedicineDTO createdMedicine = medicineService.createOrFetchMedicine(medicineDTO.getName(), medicineDTO);
            return ResponseEntity.ok(createdMedicine);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid type provided.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Invalid type provided.");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medicine by ID", description = "Retrieve a specific medicine by its unique identifier.")
    @ApiResponse(responseCode = "200", description = "Medicine found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicineDTO.class)))
    @ApiResponse(responseCode = "404", description = "Medicine not found with this id")
    public ResponseEntity<?> getMedicineById(@PathVariable Integer id) {
        try {
            MedicineDTO medicineDTO = medicineService.getMedicineById(id);
            return ResponseEntity.ok(medicineDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "List all medicines", description = "Retrieve a paginated list of all medicines in the system.")
    @ApiResponse(responseCode = "200", description = "Medicines retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    public ResponseEntity<Page<MedicineDTO>> getAllMedicines(Pageable pageable) {
        return ResponseEntity.ok(medicineService.findAllMedicines(pageable));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update medicine by ID", description = "Update the details of an existing medicine by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medicine updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicineDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error: type must be one of the predefined types.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Medicine not found with this id")
    })
    public ResponseEntity<?> updateMedicineById(@PathVariable Integer id, @Valid @RequestBody MedicineDTO medicineDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = ControllerHelper.formatBindingErrors(bindingResult);
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            MedicineDTO updatedMedicine = medicineService.updateMedicineById(id, medicineDTO);
            return ResponseEntity.ok(updatedMedicine);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Delete medicine by ID", description = "Delete a medicine by its unique identifier.")
    @ApiResponse(responseCode = "200", description = "Medicine deleted successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    @ApiResponse(responseCode = "404", description = "Medicine not found with this id")
    public ResponseEntity<String> deleteMedicine(@PathVariable Integer id) {
        try {
            medicineService.deleteMedicine(id);
            return ResponseEntity.ok("Medicine deleted successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

package com.panda.medicineinventorymanagementsystem.controller;

import com.panda.medicineinventorymanagementsystem.dto.MedicineDTO;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.mapper.MedicineMapper;
import com.panda.medicineinventorymanagementsystem.services.MedicineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/medicines")
public class MedicineController {
    private final MedicineService medicineService;
    private final MedicineMapper medicineMapper;

    public MedicineController(MedicineService medicineService, MedicineMapper medicineMapper) {
        this.medicineService = medicineService;
        this.medicineMapper = medicineMapper;
    }

    @PostMapping
    @Operation(summary = "Create a new medicine", description = "Adds a new medicine to the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medicine updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicineDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "Validation Error: Name",
                                    value = "{\"errors\": {\"name\": \"Name must not be blank\"}}"),
                            @ExampleObject(name = "Validation Error: Type",
                                    value = "{\"errors\": {\"type\": \"Type must be one of the predefined types: PRES, OTC, OTHER\"}}"),
                            @ExampleObject(name = "Validation Error: Description",
                                    value = "{\"errors\": {\"description\": \"Description must not exceed 250 characters\"}}")
                    })),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate medicine name.", content = @Content())
    })
    public ResponseEntity<MedicineDTO> createMedicine(@Valid @RequestBody MedicineDTO medicineDTO) {
        Medicine createdMedicine = medicineService.createOrFetchMedicine(medicineDTO.getName(), medicineDTO);
        return ResponseEntity.ok(medicineMapper.toDTO(createdMedicine));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medicine by ID", description = "Retrieve a specific medicine by its unique identifier.")
    @ApiResponse(responseCode = "200", description = "Medicine found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicineDTO.class)))
    @ApiResponse(responseCode = "404", description = "Medicine not found with this id")
    public ResponseEntity<MedicineDTO> getMedicineById(@PathVariable Integer id) {
        Medicine medicine = medicineService.getMedicineById(id);
        return ResponseEntity.ok(medicineMapper.toDTO(medicine));
    }

    @GetMapping
    @Operation(summary = "List all medicines", description = "Retrieve a paginated list of all medicines in the system.")
    @ApiResponse(responseCode = "200", description = "Medicines retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    public ResponseEntity<Page<MedicineDTO>> getAllMedicines(Pageable pageable) {
        Page<Medicine> medicines = medicineService.findAllMedicines(pageable);
        return ResponseEntity.ok(medicines.map(medicineMapper::toDTO));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Update medicine by ID", description = "Update the details of an existing medicine by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medicine updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MedicineDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "Validation Error: Name",
                                    value = "{\"errors\": {\"name\": \"Name must not be blank\"}}"),
                            @ExampleObject(name = "Validation Error: Type",
                                    value = "{\"errors\": {\"type\": \"Type must be one of the predefined types: PRES, OTC, OTHER\"}}"),
                            @ExampleObject(name = "Validation Error: Description",
                                    value = "{\"errors\": {\"description\": \"Description must not exceed 250 characters\"}}")
                    })),
            @ApiResponse(responseCode = "409", description = "Conflict, duplicate medicine name.", content = @Content())
    })
    public ResponseEntity<MedicineDTO> updateMedicineById(@PathVariable Integer id, @Valid @RequestBody MedicineDTO medicineDTO) {
        Medicine updatedMedicine = medicineService.updateMedicineById(id, medicineDTO);
        return ResponseEntity.ok(medicineMapper.toDTO(updatedMedicine));

    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Delete medicine by ID", description = "Delete a medicine by its unique identifier.")
    @ApiResponse(responseCode = "200", description = "Medicine deleted successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    @ApiResponse(responseCode = "404", description = "Medicine not found with this id")
    public ResponseEntity<String> deleteMedicine(@PathVariable Integer id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.ok("Medicine deleted successfully.");
    }
}

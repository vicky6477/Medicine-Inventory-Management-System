package com.panda.medicineinventorymanagementsystem.controller;

import com.panda.medicineinventorymanagementsystem.dto.MedicineDTO;
import com.panda.medicineinventorymanagementsystem.services.MedicineService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/medicines")
public class MedicineController {
    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @PostMapping
    public ResponseEntity<?> createMedicine(@Valid @RequestBody MedicineDTO medicineDTO) {
        try {
            MedicineDTO createdMedicine = medicineService.createOrFetchMedicine(medicineDTO.getName(), medicineDTO);
            return ResponseEntity.ok(createdMedicine);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid type provided.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the medicine.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicineById(@PathVariable Integer id) {
        Optional<MedicineDTO> medicineOptional = medicineService.getMedicineById(id);
        if (medicineOptional.isPresent()) {
            return ResponseEntity.ok(medicineOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine not found with ID: " + id);
        }
    }

    @GetMapping
    public ResponseEntity<Page<MedicineDTO>> getAllMedicines(Pageable pageable) {
        return ResponseEntity.ok(medicineService.findAllMedicines(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedicineById(@PathVariable Integer id, @Valid @RequestBody MedicineDTO medicineDTO) {
        Optional<MedicineDTO> updatedMedicine = medicineService.updateMedicineById(id, medicineDTO);
        if (updatedMedicine.isPresent()) {
            return ResponseEntity.ok(updatedMedicine.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine id not found: " + id);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedicine(@PathVariable Integer id) {
        try {
            medicineService.deleteMedicine(id);
            return ResponseEntity.ok("Medicine deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

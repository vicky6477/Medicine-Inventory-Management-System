package com.panda.medicineinventorymanagementsystem.controller;

import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.Type;
import com.panda.medicineinventorymanagementsystem.services.MedicineService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@RestController
@RequestMapping("/medicines")
public class MedicineController {
    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

//    @PostMapping
//    public ResponseEntity<Medicine> createMedicine(@RequestParam(required = false) String searchParam, @RequestBody Medicine medicine) {
//        Medicine createdMedicine = medicineService.createOrFetchMedicine(searchParam, medicine);
//        return ResponseEntity.ok(createdMedicine);
//    }

    @PostMapping
    public ResponseEntity<?> createMedicine(@RequestBody Medicine medicine) {
        try {
            medicine.setQuantity(0);
            Medicine createdMedicine = medicineService.createOrFetchMedicine(medicine.getName(), medicine);
            return ResponseEntity.ok(createdMedicine);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid type provided.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the medicine.");
        }
    }



    //    @GetMapping("/{id}")
//    public ResponseEntity<Medicine> getMedicineById(@PathVariable Integer id){
//        return ResponseEntity.ok(medicineService.getMedicineById(id));
//    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicineById(@PathVariable Integer id) {
        Optional<Medicine> medicineOptional = medicineService.getMedicineById(id);
        if (medicineOptional.isPresent()) {
            return ResponseEntity.ok(medicineOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Medicine not found with ID: " + id);
        }
    }

    @GetMapping
    public ResponseEntity<Page<Medicine>> getAllMedicines(Pageable pageable) {
        return ResponseEntity.ok(medicineService.findAllMedicines(pageable));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Medicine> updateMedicineById(@PathVariable Integer id, @RequestBody Medicine medicine) {
//        Medicine updatedMedicine = medicineService.updateMedicineById(id, medicine);
//        return ResponseEntity.ok(updatedMedicine);
//    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedicineById(@PathVariable Integer id, @RequestBody Medicine medicine) {
        Optional<Medicine> updatedMedicine = medicineService.updateMedicineById(id, medicine);
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
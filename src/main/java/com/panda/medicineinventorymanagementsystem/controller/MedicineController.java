package com.panda.medicineinventorymanagementsystem.controller;

import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import com.panda.medicineinventorymanagementsystem.services.MedicineService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
    public ResponseEntity<Medicine> createMedicine(@RequestParam String name) {
        Medicine newMedicine = new Medicine();
        newMedicine.setName(name);
        newMedicine.setQuantity(0);
        Medicine createdMedicine = medicineService.createOrFetchMedicine(name, newMedicine);
        return ResponseEntity.ok(createdMedicine);
    }


    //    @GetMapping("/{id}")
//    public ResponseEntity<Medicine> getMedicineById(@PathVariable Integer id){
//        return ResponseEntity.ok(medicineService.getMedicineById(id));
//    }
    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable Integer id) {
        try {
            Medicine medicine = medicineService.getMedicineById(id);
            return ResponseEntity.ok(medicine);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 更明确的错误处理
        }
    }


    @GetMapping
    public ResponseEntity<Page<Medicine>> getAllMedicines(Pageable pageable) {
        return ResponseEntity.ok(medicineService.findAllMedicines(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medicine> updateMedicine(@PathVariable Integer id, @RequestBody Medicine medicine) {
        Medicine updatedMedicine = medicineService.updateMedicine(id, medicine);
        return ResponseEntity.ok(updatedMedicine);
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

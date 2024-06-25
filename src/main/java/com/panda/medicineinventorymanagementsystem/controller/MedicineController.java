package com.panda.medicineinventorymanagementsystem.controller;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.services.MedicineService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/medicines")
public class MedicineController {
    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @PostMapping
    public ResponseEntity<Medicine> createMedicine(@RequestBody Medicine medicine){
        return ResponseEntity.ok(medicineService.createMedicine(medicine));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable Integer id){
        return ResponseEntity.ok(medicineService.getMedicineById(id));
    }

    @GetMapping
    public Page<Medicine> getAllMedicines(Pageable pageable) {
        return medicineService.findAllMedicines(pageable);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medicine> updateMedicine(@PathVariable Integer id, @RequestBody Medicine medicine) {
        Medicine updatedMedicine = medicineService.updateMedicine(id, medicine);
        return ResponseEntity.ok(updatedMedicine);
    }
}

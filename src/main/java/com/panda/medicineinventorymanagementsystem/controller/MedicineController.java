package com.panda.medicineinventorymanagementsystem.controller;
import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import com.panda.medicineinventorymanagementsystem.services.MedicineService;
import org.springframework.data.domain.Page;
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

    @PostMapping
    public ResponseEntity<Medicine> createMedicine(@RequestBody Medicine medicine){
        return ResponseEntity.ok(medicineService.createMedicine(medicine));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable Integer id){
        return ResponseEntity.ok(medicineService.getMedicineById(id));
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

    @PostMapping("/inbound/transactions")
    public ResponseEntity<List<Medicine>> addInboundTransactions(@RequestBody List<InboundTransaction> transactions) {
        return ResponseEntity.ok(medicineService.addInboundTransactions(transactions));
    }

    @GetMapping("/inbound/transactions")
    public ResponseEntity<Page<InboundTransaction>> getAllInboundTransactions(Pageable pageable) {
        return ResponseEntity.ok(medicineService.getAllInboundTransactions(pageable));
    }

    @PostMapping("/outbound/transactions")
    public ResponseEntity<List<Medicine>> addOutboundTransactions(@RequestBody List<OutboundTransaction> transactions) {
        return ResponseEntity.ok(medicineService.addOutboundTransactions(transactions));
    }

    @GetMapping("/outbound/transactions")
    public ResponseEntity<Page<OutboundTransaction>> getAllOutboundTransactions(Pageable pageable) {
        return ResponseEntity.ok(medicineService.getAllOutboundTransactions(pageable));
    }
}

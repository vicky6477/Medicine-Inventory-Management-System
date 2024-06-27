package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.OutboundTransaction;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Service
public class MedicineService {
    private final MedicineRepository medicineRepository;

    //Constructor injection
    @Autowired
    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    //Create medicine
    public Medicine createMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    //Retrieve a medicine by its ID.
    public Medicine getMedicineById(Integer id) {
        return medicineRepository.findById(id).orElseThrow(() -> new RuntimeException("id not found"));
    }

    //Retrieve all medicines, with the ability to paginate the results.
    public Page<Medicine> findAllMedicines(Pageable pageable) {
        return medicineRepository.findAll(pageable);
    }

    //Updates the details of an existing medicine
    public Medicine updateMedicine(Integer id, Medicine medicineDetails) {
        //Retrieve the medicine by its id, if medicine does not exit, throw exception
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
        medicine.setName(medicineDetails.getName());
        medicine.setDescription(medicineDetails.getDescription());
        medicine.setQuantity(medicineDetails.getQuantity());
        medicine.setType(medicineDetails.getType());
        return medicineRepository.save(medicine);
    }
}

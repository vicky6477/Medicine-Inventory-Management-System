package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.repository.InboundTransactionRepository;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import com.panda.medicineinventorymanagementsystem.repository.OutboundTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;


@Service
public class MedicineService {
    private final MedicineRepository medicineRepository;
    //Constructor injection
    @Autowired
    public MedicineService(MedicineRepository medicineRepository, InboundTransactionRepository inboundTransactionRepository, OutboundTransactionRepository outboundTransactionRepository) {
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

    //delete an existing medicine by id
    public void deleteMedicine(Integer id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + id));
        medicineRepository.delete(medicine);
    }
}

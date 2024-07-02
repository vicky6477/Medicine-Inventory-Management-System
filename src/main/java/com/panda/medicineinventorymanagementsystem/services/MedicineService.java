package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.Type;
import com.panda.medicineinventorymanagementsystem.repository.InboundTransactionRepository;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import com.panda.medicineinventorymanagementsystem.repository.OutboundTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


@Service
public class MedicineService {
    private final MedicineRepository medicineRepository;
    private final OpenFDAApiService openFDAApiService;
    private final InboundTransactionRepository inboundTransactionRepository;
    private final OutboundTransactionRepository outboundTransactionRepository;

    @Autowired
    public MedicineService(MedicineRepository medicineRepository,
                           OpenFDAApiService openFDAApiService,
                           InboundTransactionRepository inboundTransactionRepository,
                           OutboundTransactionRepository outboundTransactionRepository) {
        this.medicineRepository = medicineRepository;
        this.openFDAApiService = openFDAApiService;
        this.inboundTransactionRepository = inboundTransactionRepository;
        this.outboundTransactionRepository = outboundTransactionRepository;
    }

    @Transactional
    public Medicine createOrFetchMedicine(String name, Medicine inputMedicine) {
        // Check if a medicine with the same name already exists in the database
        Optional<Medicine> existingMedicine = medicineRepository.findByName(name);
        if (existingMedicine.isPresent()) {
            // If the medicine already exists, throw an exception or return an error message
            throw new IllegalStateException("Medicine with name '" + name + "' already exists");
        }

        // Set default values for properties not set
        if (inputMedicine.getQuantity() == null) {
            inputMedicine.setQuantity(0); // Default quantity
        }
        if (inputMedicine.getDescription() == null) {
            inputMedicine.setDescription("Default description"); // Default description
        }
        if (inputMedicine.getType() == null) {
            inputMedicine.setType(Type.OTHER); // Default type
        }

        // Fetch medicine data from the API, if it fails, use the input medicine data
        return openFDAApiService.fetchMedicineData(name, inputMedicine).map(apiMedicine -> {
            // Supplement missing information from the API data
            if (apiMedicine.getDescription() == null || apiMedicine.getDescription().isEmpty()) {
                apiMedicine.setDescription(inputMedicine.getDescription());
            }
            if (apiMedicine.getType() == null) {
                apiMedicine.setType(inputMedicine.getType());
            }
            apiMedicine.setName(name);  // Ensure the medicine name is set
            return medicineRepository.save(apiMedicine);
        }).orElseGet(() -> medicineRepository.save(inputMedicine));
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
    @Transactional
    public void deleteMedicine(Integer id) {
        // Check for existing inbound and outbound transactions related to the medicine
        boolean existsInbound = inboundTransactionRepository.existsByMedicineId(id);
        boolean existsOutbound = outboundTransactionRepository.existsByMedicineId(id);
        if (existsInbound || existsOutbound) {
            throw new IllegalStateException("Cannot delete medicine with ID: " + id +
                    " because there are existing related transactions.");
        }

        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + id));
        medicineRepository.delete(medicine);
    }
}

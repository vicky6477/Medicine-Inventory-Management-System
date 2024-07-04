package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.repository.InboundTransactionRepository;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import com.panda.medicineinventorymanagementsystem.repository.OutboundTransactionRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public Medicine createOrFetchMedicine(String name, @Valid Medicine inputMedicine) {
        Logger logger = LoggerFactory.getLogger(MedicineService.class);

        // Check if a medicine with the same name already exists in the database
        logger.info("Checking if medicine with name '{}' already exists in the database.", name);
        Optional<Medicine> existingMedicine = medicineRepository.findByName(name);
        if (existingMedicine.isPresent()) {
            throw new IllegalStateException("Medicine with name '" + name + "' already exists");
        }

        // Set default values for properties not set
        if (inputMedicine.getQuantity() == null) {
            inputMedicine.setQuantity(0); // Default quantity
        }
        if (inputMedicine.getDescription() == null) {
            inputMedicine.setDescription("Default description"); // Default description
        }

        // Fetch medicine data from the API
        logger.info("Fetching data from API for medicine '{}'.", name);
        Optional<Medicine> fetchedMedicine = openFDAApiService.fetchMedicineData(name, inputMedicine);
        if (fetchedMedicine.isPresent()) {
            Medicine apiMedicine = fetchedMedicine.get();

            if (apiMedicine.getDescription().isEmpty()) {
                apiMedicine.setDescription(inputMedicine.getDescription());
            }
            apiMedicine.setName(name);
            return medicineRepository.save(apiMedicine);
        } else {
            return medicineRepository.save(inputMedicine);
        }
    }

    //Retrieve a medicine by its ID.
    public Optional<Medicine> getMedicineById(Integer id) {
        return medicineRepository.findById(id);
    }
    //Retrieve all medicines, with the ability to paginate the results.
    public Page<Medicine> findAllMedicines(Pageable pageable) {
        return medicineRepository.findAll(pageable);
    }

/*    Updates the details of an existing medicine
    public Medicine updateMedicineById(Integer id, Medicine medicineDetails) {
        //Retrieve the medicine by its id, if medicine does not exit, throw exception
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));
        medicine.setName(medicineDetails.getName());
        medicine.setDescription(medicineDetails.getDescription());
        medicine.setQuantity(medicineDetails.getQuantity());
        medicine.setType(medicineDetails.getType());
        return medicineRepository.save(medicine);
    }*/

    public Optional<Medicine> updateMedicineById(Integer id, @Valid Medicine medicineDetails) {
        return medicineRepository.findById(id)
                .map(existingMedicine -> {
                    existingMedicine.setName(medicineDetails.getName());
                    existingMedicine.setDescription(medicineDetails.getDescription());
                    existingMedicine.setQuantity(medicineDetails.getQuantity());
                    existingMedicine.setType(medicineDetails.getType());
                    return medicineRepository.save(existingMedicine);
                });
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
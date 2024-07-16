package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.dto.MedicineDTO;
import com.panda.medicineinventorymanagementsystem.mapper.MedicineMapper;
import com.panda.medicineinventorymanagementsystem.repository.InboundTransactionRepository;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import com.panda.medicineinventorymanagementsystem.repository.OutboundTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class MedicineService {
    private final MedicineRepository medicineRepository;
    private final OpenFDAApiService openFDAApiService;
    private final InboundTransactionRepository inboundTransactionRepository;
    private final OutboundTransactionRepository outboundTransactionRepository;
    private final MedicineMapper medicineMapper;


    @Autowired
    public MedicineService(MedicineRepository medicineRepository,
                           OpenFDAApiService openFDAApiService,
                           InboundTransactionRepository inboundTransactionRepository,
                           OutboundTransactionRepository outboundTransactionRepository,
                           MedicineMapper medicineMapper) {
        this.medicineRepository = medicineRepository;
        this.openFDAApiService = openFDAApiService;
        this.inboundTransactionRepository = inboundTransactionRepository;
        this.outboundTransactionRepository = outboundTransactionRepository;
        this.medicineMapper = medicineMapper;
    }


    /**
     * Creates or fetches a medicine based on the provided name and details.
     * If the medicine already exists, it throws an exception; otherwise, it fetches data from openFDA and saves it.
     *
     * @param name the name of the medicine to be checked or created
     * @param inputMedicineDTO the data transfer object containing medicine details
     * @return the newly created or fetched medicine entity
     * @throws IllegalStateException if a medicine with the same name already exists
     */
    @Transactional
    public Medicine createOrFetchMedicine(String name, MedicineDTO inputMedicineDTO) {
        //check if the medicine already exits
        if (medicineRepository.findByName(name).isPresent()) {
            throw new IllegalStateException("Medicine with name '" + name + "' already exists");
        }

        // Set default values for properties not set
        if (inputMedicineDTO.getQuantity() == null) {
            inputMedicineDTO.setQuantity(0);
        }
        if (inputMedicineDTO.getDescription() == null) {
            inputMedicineDTO.setDescription("Default description");
        }

        //fetch medicine from openFDA
        Optional<MedicineDTO> fetchedMedicineDTO = openFDAApiService.fetchMedicineData(name, inputMedicineDTO);
        Medicine medicine = medicineMapper.toEntity(fetchedMedicineDTO.orElse(inputMedicineDTO));
        medicineRepository.save(medicine);
        return medicine;
    }

    /**
     * Retrieves a medicine entity by its ID.
     *
     * @param id the identifier of the medicine
     * @return the found medicine entity
     * @throws EntityNotFoundException if no medicine is found with the provided ID
     */
    public Medicine getMedicineById(Integer id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medicine not found with ID: " + id));
    }


    /**
     * Retrieves a paginated list of all medicines in the system. This method leverages Spring Data's
     * Pageable interface to encapsulate pagination and sorting behavior.
     *
     * @param pageable a Pageable object that specifies the page request details such as size, sort, and page number
     * @return a page of Medicine entities based on the given pagination parameters
     */
    public Page<Medicine> findAllMedicines(Pageable pageable) {
        return medicineRepository.findAll(pageable);
    }


    /**
     * Updates an existing medicine identified by ID with the provided details.
     *
     * @param id the identifier of the medicine to update
     * @param medicineDTO the data transfer object containing updated details
     * @return the updated medicine entity
     * @throws EntityNotFoundException if no medicine is found with the provided ID
     */
    @Transactional
    public Medicine updateMedicineById(Integer id, MedicineDTO medicineDTO) {
        Medicine existingMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medicine not found with ID: " + id));

        medicineMapper.updateEntityFromDTO(medicineDTO, existingMedicine);
        return medicineRepository.save(existingMedicine);
    }


    /**
     * Deletes a medicine by its ID. Checks for existing related transactions and throws an exception if found.
     *
     * @param id the identifier of the medicine to delete
     * @throws EntityNotFoundException if the medicine cannot be deleted due to existing transactions
     */
    @Transactional
    public void deleteMedicine(Integer id) {
        // Check if the medicine exists
        Medicine existingMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No medicine found with ID: " + id));

        // Check if there are existing related transactions
        boolean existsInbound = inboundTransactionRepository.existsByMedicineId(id);
        boolean existsOutbound = outboundTransactionRepository.existsByMedicineId(id);
        if (existsInbound || existsOutbound) {
            throw new IllegalStateException("Cannot delete medicine with ID: " + id + " because there are existing related transactions.");
        }
        medicineRepository.deleteById(id);
    }
}

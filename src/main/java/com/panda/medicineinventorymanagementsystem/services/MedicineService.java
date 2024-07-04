package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.dto.MedicineDTO;
import com.panda.medicineinventorymanagementsystem.entity.Type;
import com.panda.medicineinventorymanagementsystem.repository.InboundTransactionRepository;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import com.panda.medicineinventorymanagementsystem.repository.OutboundTransactionRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MedicineService {
    private static final Logger logger = LoggerFactory.getLogger(MedicineService.class);
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
    public MedicineDTO createOrFetchMedicine(String name, MedicineDTO inputMedicineDTO) {
        logger.info("Attempting to create or fetch medicine: {}", name);
        if (medicineRepository.findByName(name).isPresent()) {
            logger.error("Creation failed: Medicine with name '{}' already exists.", name);
            throw new IllegalStateException("Medicine with name '" + name + "' already exists");
        }

        // Set default values for properties not set
        if (inputMedicineDTO.getQuantity() == null) {
            inputMedicineDTO.setQuantity(0); // Default quantity
        }
        if (inputMedicineDTO.getDescription() == null) {
            inputMedicineDTO.setDescription("Default description"); // Default description
        }

        Optional<MedicineDTO> fetchedMedicineDTO = openFDAApiService.fetchMedicineData(name, inputMedicineDTO);
        fetchedMedicineDTO.ifPresentOrElse(
                dto -> logger.info("Data fetched from FDA for medicine: {}", name),
                () -> logger.info("No data fetched from FDA, using default data for medicine: {}", name)
        );

        Medicine medicine = convertToEntity(fetchedMedicineDTO.orElse(inputMedicineDTO));
        medicineRepository.save(medicine);
        logger.info("Medicine created or fetched successfully with ID: {}", medicine.getId());
        return convertToDTO(medicine);
    }

    public Optional<MedicineDTO> getMedicineById(Integer id) {
        logger.info("Retrieving medicine by ID: {}", id);
        return medicineRepository.findById(id).map(this::convertToDTO);
    }

    public Page<MedicineDTO> findAllMedicines(Pageable pageable) {
        logger.info("Retrieving all medicines with pagination");
        return medicineRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Transactional
    public Optional<MedicineDTO> updateMedicineById(Integer id, @Valid MedicineDTO medicineDetailsDTO) {
        logger.info("Updating medicine ID: {}", id);
        return medicineRepository.findById(id).map(existingMedicine -> {
            updateEntity(existingMedicine, medicineDetailsDTO);
            Medicine updatedMedicine = medicineRepository.save(existingMedicine);
            logger.info("Medicine updated successfully with ID: {}", updatedMedicine.getId());
            return convertToDTO(updatedMedicine);
        });
    }

    @Transactional
    public void deleteMedicine(Integer id) {
        logger.info("Attempting to delete medicine ID: {}", id);
        boolean existsInbound = inboundTransactionRepository.existsByMedicineId(id);
        boolean existsOutbound = outboundTransactionRepository.existsByMedicineId(id);
        if (existsInbound || existsOutbound) {
            logger.error("Deletion failed: Medicine with ID: {} has existing related transactions.", id);
            throw new IllegalStateException("Cannot delete medicine with ID: " + id + " because there are existing related transactions.");
        }
        medicineRepository.deleteById(id);
        logger.info("Medicine deleted successfully with ID: {}", id);
    }

    private MedicineDTO convertToDTO(Medicine medicine) {
        logger.debug("Converting Medicine to MedicineDTO with ID: {}", medicine.getId());
        MedicineDTO dto = new MedicineDTO();
        dto.setId(medicine.getId());
        dto.setName(medicine.getName());
        dto.setDescription(medicine.getDescription());
        dto.setQuantity(medicine.getQuantity());
        dto.setType(medicine.getType().name());
        return dto;
    }

    private Medicine convertToEntity(MedicineDTO dto) {
        logger.debug("Converting MedicineDTO to Medicine with ID: {}", dto.getId());
        Medicine medicine = new Medicine();
        medicine.setId(dto.getId());
        medicine.setName(dto.getName());
        medicine.setDescription(dto.getDescription());
        medicine.setQuantity(dto.getQuantity());
        medicine.setType(Enum.valueOf(Type.class, dto.getType()));
        return medicine;
    }

    private void updateEntity(Medicine medicine, MedicineDTO dto) {
        logger.debug("Updating entity from DTO for ID: {}", dto.getId());
        if (dto.getName() != null) medicine.setName(dto.getName());
        if (dto.getDescription() != null) medicine.setDescription(dto.getDescription());
        if (dto.getQuantity() != null) medicine.setQuantity(dto.getQuantity());
        if (dto.getType() != null) medicine.setType(Enum.valueOf(Type.class, dto.getType()));
    }
}

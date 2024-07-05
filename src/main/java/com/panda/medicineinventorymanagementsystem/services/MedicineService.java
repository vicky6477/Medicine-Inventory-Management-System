package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.dto.MedicineDTO;
import com.panda.medicineinventorymanagementsystem.entity.Type;
import com.panda.medicineinventorymanagementsystem.repository.InboundTransactionRepository;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import com.panda.medicineinventorymanagementsystem.repository.OutboundTransactionRepository;
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

    //create a new medicine
    @Transactional
    public MedicineDTO createOrFetchMedicine(String name, MedicineDTO inputMedicineDTO) {
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
        Medicine medicine = convertToEntity(fetchedMedicineDTO.orElse(inputMedicineDTO));
        medicineRepository.save(medicine);
        return convertToDTO(medicine);
    }

    //retrieve a medicine by id
    public MedicineDTO getMedicineById(Integer id) {
        return medicineRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + id));
    }

    //retrieve all medicine
    public Page<MedicineDTO> findAllMedicines(Pageable pageable) {
        return medicineRepository.findAll(pageable).map(this::convertToDTO);
    }

    //update an existing medicine by id

    @Transactional
    public Optional<MedicineDTO> updateMedicineById(Integer id, MedicineDTO medicineDetailsDTO) {
        Optional<Medicine> foundMedicine = medicineRepository.findById(id);

        if (!foundMedicine.isPresent()) {
            throw new IllegalStateException("Medicine not found with ID: " + id);
        }

        Medicine existingMedicine = foundMedicine.get();
        updateEntity(existingMedicine, medicineDetailsDTO);
        Medicine updatedMedicine = medicineRepository.save(existingMedicine);
        return Optional.of(convertToDTO(updatedMedicine));
    }


    //delete an existing medicine by id
    @Transactional
    public void deleteMedicine(Integer id) {
        boolean existsInbound = inboundTransactionRepository.existsByMedicineId(id);
        boolean existsOutbound = outboundTransactionRepository.existsByMedicineId(id);
        if (existsInbound || existsOutbound) {
            throw new IllegalStateException("Cannot delete medicine with ID: " + id + " because there are existing related transactions.");
        }
        medicineRepository.deleteById(id);
    }

    // Convert Entity to DTO
    private MedicineDTO convertToDTO(Medicine medicine) {
        MedicineDTO dto = new MedicineDTO();
        dto.setId(medicine.getId());
        dto.setName(medicine.getName());
        dto.setDescription(medicine.getDescription());
        dto.setQuantity(medicine.getQuantity());
        dto.setType(medicine.getType().name());
        return dto;
    }

    // Convert DTO to Entity
    private Medicine convertToEntity(MedicineDTO dto) {
        Medicine medicine = new Medicine();
        medicine.setId(dto.getId());
        medicine.setName(dto.getName());
        medicine.setDescription(dto.getDescription());
        medicine.setQuantity(dto.getQuantity());
        medicine.setType(Enum.valueOf(Type.class, dto.getType()));
        return medicine;
    }


    // Update Entity with DTO
    private void updateEntity(Medicine medicine, MedicineDTO dto) {
        if (dto.getName() != null) medicine.setName(dto.getName());
        if (dto.getDescription() != null) medicine.setDescription(dto.getDescription());
        if (dto.getQuantity() != null) medicine.setQuantity(dto.getQuantity());
        if (dto.getType() != null) medicine.setType(Enum.valueOf(Type.class, dto.getType()));
    }
}

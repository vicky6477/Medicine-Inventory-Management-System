package com.panda.medicineinventorymanagementsystem.services;

import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.dto.MedicineDTO;
import com.panda.medicineinventorymanagementsystem.entity.Type;
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

    /**
     * Constructs a MedicineService with required repositories.
     * @param medicineRepository the repository for medicine data access
     * @param openFDAApiService the service for fetching medicine data from OpenFDA
     * @param inboundTransactionRepository the repository for inbound transactions
     * @param outboundTransactionRepository the repository for outbound transactions
     */
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

    /**
     * Creates or fetches medicine from OpenFDA and saves it in the database.
     * @param name the name of the medicine
     * @param inputMedicineDTO the DTO containing medicine data
     * @return the saved medicine DTO
     * @throws IllegalStateException if the medicine name already exists
     * @throws IllegalArgumentException if the provided type is invalid
     */
    @Transactional
    public MedicineDTO createOrFetchMedicine(String name, MedicineDTO inputMedicineDTO) {
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
        Medicine medicine = convertToEntity(fetchedMedicineDTO.orElse(inputMedicineDTO));
        medicineRepository.save(medicine);
        return convertToDTO(medicine);
    }


    /**
     * Retrieves a medicine by its ID.
     * @param id the ID of the medicine
     * @return the medicine DTO
     * @throws EntityNotFoundException if no medicine is found with the provided ID
     */
    public MedicineDTO getMedicineById(Integer id) {
        return medicineRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Medicine not found with ID: " + id));
    }

    /**
     * Retrieves all medicines in a paginated format.
     * @param pageable the pagination information.
     * @return a page of medicine DTOs.
     */
    public Page<MedicineDTO> findAllMedicines(Pageable pageable) {
        return medicineRepository.findAll(pageable).map(this::convertToDTO);
    }

    /**
     * Updates an existing medicine by its ID.
     * @param id the ID of the medicine to update
     * @param medicineDTO the DTO containing updated data
     * @return the updated medicine DTO
     * @throws EntityNotFoundException if no medicine is found with the provided ID
     * @throws IllegalArgumentException if the provided type is invalid
     */
    @Transactional
    public MedicineDTO updateMedicineById(Integer id, MedicineDTO medicineDTO) {
        Medicine existingMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medicine not found with ID: " + id));

        updateEntity(existingMedicine, medicineDTO);
        Medicine updatedMedicine = medicineRepository.save(existingMedicine);
        return convertToDTO(updatedMedicine);
    }

    /**
     * Deletes a medicine by its ID
     * @param id the ID of the medicine to delete.
     * @throws EntityNotFoundException if there are existing related transactions that prevent deletion.
     */
    @Transactional
    public void deleteMedicine(Integer id) {
        //check if there are existing related transactions
        boolean existsInbound = inboundTransactionRepository.existsByMedicineId(id);
        boolean existsOutbound = outboundTransactionRepository.existsByMedicineId(id);
        if (existsInbound || existsOutbound) {
            throw new EntityNotFoundException("Cannot delete medicine with ID: " + id + " because there are existing related transactions.");
        }
        medicineRepository.deleteById(id);
    }

    /**
     * Converts a Medicine entity to a MedicineDTO.
     * @param medicine the Medicine entity to convert.
     * @return a MedicineDTO containing the data from the entity.
     */
    private MedicineDTO convertToDTO(Medicine medicine) {
        MedicineDTO dto = new MedicineDTO();
        dto.setId(medicine.getId());
        dto.setName(medicine.getName());
        dto.setDescription(medicine.getDescription());
        dto.setQuantity(medicine.getQuantity());
        dto.setType(medicine.getType().name());
        return dto;
    }

    /**
     * Converts a MedicineDTO to a Medicine entity.
     * @param dto the MedicineDTO to convert.
     * @return a Medicine entity containing the data from the DTO.
     */
    private Medicine convertToEntity(MedicineDTO dto) {
        Medicine medicine = new Medicine();
        medicine.setId(dto.getId());
        medicine.setName(dto.getName());
        medicine.setDescription(dto.getDescription());
        medicine.setQuantity(dto.getQuantity());
        medicine.setType(Enum.valueOf(Type.class, dto.getType()));
        return medicine;
    }


    /**
     * Updates an existing Medicine entity with data from a MedicineDTO.
     * @param medicine the existing Medicine entity to update.
     * @param dto the MedicineDTO containing updated data.
     */
    private void updateEntity(Medicine medicine, MedicineDTO dto) {
        if (dto.getName() != null) medicine.setName(dto.getName());
        if (dto.getDescription() != null) medicine.setDescription(dto.getDescription());
        if (dto.getQuantity() != null) medicine.setQuantity(dto.getQuantity());
        if (dto.getType() != null) medicine.setType(Enum.valueOf(Type.class, dto.getType()));
    }
}

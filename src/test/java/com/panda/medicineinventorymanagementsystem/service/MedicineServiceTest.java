package com.panda.medicineinventorymanagementsystem.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.panda.medicineinventorymanagementsystem.dto.MedicineDTO;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.mapper.MedicineMapper;
import com.panda.medicineinventorymanagementsystem.repository.InboundTransactionRepository;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import com.panda.medicineinventorymanagementsystem.repository.OutboundTransactionRepository;
import com.panda.medicineinventorymanagementsystem.services.MedicineService;
import com.panda.medicineinventorymanagementsystem.services.OpenFDAApiService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    InboundTransactionRepository inboundTransactionRepository;

    @Mock
    OutboundTransactionRepository outboundTransactionRepository;

    @Mock
    private OpenFDAApiService openFDAApiService;

    @Mock
    private MedicineMapper medicineMapper;

    @InjectMocks
    private MedicineService medicineService;

    @Test
    void createOrFetchMedicine_whenMedicineDoesNotExist() {
        String medicineName = "Aspirin";
        MedicineDTO dto = new MedicineDTO();
        dto.setName(medicineName);
        dto.setDescription("Pain relief");
        Medicine medicine = new Medicine();

        when(medicineRepository.findByName(medicineName)).thenReturn(Optional.empty());
        when(openFDAApiService.fetchMedicineData(any(), any())).thenReturn(Optional.of(dto));
        when(medicineMapper.toEntity(any(MedicineDTO.class))).thenReturn(medicine);
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        Medicine result = medicineService.createOrFetchMedicine(medicineName, dto);

        verify(medicineRepository).save(medicine);
        verify(openFDAApiService).fetchMedicineData(medicineName, dto);
        assertNotNull(result);
    }

    @Test
    void createOrFetchMedicine_whenMedicineExists() {
        String medicineName = "Aspirin";
        MedicineDTO dto = new MedicineDTO();
        when(medicineRepository.findByName(medicineName)).thenReturn(Optional.of(new Medicine()));

        assertThrows(IllegalStateException.class, () -> medicineService.createOrFetchMedicine(medicineName, dto));
    }

    @Test
    void getMedicineById_whenFound() {
        Integer id = 1;
        Medicine medicine = new Medicine();
        when(medicineRepository.findById(id)).thenReturn(Optional.of(medicine));

        Medicine result = medicineService.getMedicineById(id);

        assertNotNull(result);
        assertEquals(medicine, result);
    }

    @Test
    void getMedicineById_whenNotFound() {
        Integer id = 1;
        when(medicineRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> medicineService.getMedicineById(id));
    }

    @Test
    void findAllMedicines() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Medicine> page = new PageImpl<>(Collections.singletonList(new Medicine()));
        when(medicineRepository.findAll(pageRequest)).thenReturn(page);

        Page<Medicine> result = medicineService.findAllMedicines(pageRequest);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void updateMedicineById_whenMedicineExists() {
        Integer id = 1;
        Medicine existingMedicine = new Medicine();
        MedicineDTO medicineDTO = new MedicineDTO();
        when(medicineRepository.findById(id)).thenReturn(Optional.of(existingMedicine));
        when(medicineRepository.save(existingMedicine)).thenReturn(existingMedicine);

        Medicine result = medicineService.updateMedicineById(id, medicineDTO);

        assertNotNull(result);
        verify(medicineMapper).updateEntityFromDTO(medicineDTO, existingMedicine);
        verify(medicineRepository).save(existingMedicine);
    }

    @Test
    void updateMedicineById_whenMedicineNotFound() {
        Integer id = 1;
        MedicineDTO medicineDTO = new MedicineDTO();
        when(medicineRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> medicineService.updateMedicineById(id, medicineDTO));
    }

    @Test
    void deleteMedicine_whenMedicineExistsAndNoTransactions() {
        Integer id = 1;
        Medicine medicine = new Medicine();
        when(medicineRepository.findById(id)).thenReturn(Optional.of(medicine));
        when(inboundTransactionRepository.existsByMedicineId(id)).thenReturn(false);
        when(outboundTransactionRepository.existsByMedicineId(id)).thenReturn(false);

        medicineService.deleteMedicine(id);

        verify(medicineRepository).deleteById(id);
    }

    @Test
    void deleteMedicine_whenMedicineHasTransactions() {
        Integer id = 1;
        Medicine medicine = new Medicine();
        when(medicineRepository.findById(id)).thenReturn(Optional.of(medicine));
        when(inboundTransactionRepository.existsByMedicineId(id)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> medicineService.deleteMedicine(id));
    }

    @Test
    void deleteMedicine_whenMedicineNotFound() {
        Integer id = 1;
        when(medicineRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> medicineService.deleteMedicine(id));
    }
}


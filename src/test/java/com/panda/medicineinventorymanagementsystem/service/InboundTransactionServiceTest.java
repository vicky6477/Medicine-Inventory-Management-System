package com.panda.medicineinventorymanagementsystem.service;

import com.panda.medicineinventorymanagementsystem.dto.InboundTransactionDTO;
import com.panda.medicineinventorymanagementsystem.entity.InboundTransaction;
import com.panda.medicineinventorymanagementsystem.entity.Medicine;
import com.panda.medicineinventorymanagementsystem.entity.User;
import com.panda.medicineinventorymanagementsystem.mapper.InboundTransactionMapper;
import com.panda.medicineinventorymanagementsystem.repository.InboundTransactionRepository;
import com.panda.medicineinventorymanagementsystem.repository.MedicineRepository;
import com.panda.medicineinventorymanagementsystem.services.InboundTransactionService;
import com.panda.medicineinventorymanagementsystem.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class InboundTransactionServiceTest {

    @Mock
    private InboundTransactionRepository inboundTransactionRepository;

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private InboundTransactionMapper inboundTransactionMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private InboundTransactionService inboundTransactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    void testAddInboundTransactions() {
//        // Arrange
//        InboundTransactionDTO dto = new InboundTransactionDTO(null, 1, 1, 10, 50, 60, new Date(), "Example");
//        User user = new User();
//        user.setId(1);
//        Medicine medicine = new Medicine();
//        medicine.setId(1);
//        medicine.setQuantity(50);
//
//        InboundTransaction transaction = new InboundTransaction();
//        transaction.setMedicine(medicine);
//        transaction.setUser(user);
//        transaction.setQuantity(10);
//
//        // Mocks setup
//        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
//        when(medicineRepository.findAllById(Collections.singleton(1))).thenReturn(Collections.singletonList(medicine));
//        when(inboundTransactionMapper.toEntity(any(), any(), any())).thenReturn(transaction);
//
//        // Act
//        List<InboundTransaction> transactions = inboundTransactionService.addInboundTransactions(Collections.singletonList(dto));
//
//        // Assert
//        assertNotNull(transactions, "Transaction list should not be null");
//        assertFalse(transactions.isEmpty(), "Transactions list should not be empty");
//        assertEquals(1, transactions.size(), "There should be one transaction in the list");
//        verify(inboundTransactionRepository).saveAll(anyList());
//
//    }


    @Test
    void testGetAllInboundTransactions() {
        Pageable pageable = mock(Pageable.class);
        User user = new User();
        InboundTransaction transaction = new InboundTransaction();
        Page<InboundTransaction> page = new PageImpl<>(Arrays.asList(transaction));

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(inboundTransactionRepository.findAllByUser(eq(user), eq(pageable))).thenReturn(page);

        Page<InboundTransaction> result = inboundTransactionService.getAllInboundTransactions(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetInboundTransactionById() {
        User user = new User();
        InboundTransaction transaction = new InboundTransaction();
        transaction.setId(1);

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(inboundTransactionRepository.findByIdAndUser(1, user)).thenReturn(Optional.of(transaction));

        InboundTransaction result = inboundTransactionService.getInboundTransactionById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void testGetInboundTransactionById_NotFound() {
        User user = new User();

        when(userService.getCurrentAuthenticatedUser()).thenReturn(user);
        when(inboundTransactionRepository.findByIdAndUser(1, user)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> inboundTransactionService.getInboundTransactionById(1));
    }
}
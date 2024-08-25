package com.hsbc.service;

import com.hsbc.dao.DeliveryScheduleDao;
import com.hsbc.exception.delivery.DeliveryScheduleNotFoundException;
import com.hsbc.model.DeliverySchedule;
import com.hsbc.model.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeliveryScheduleServiceTest {

    @Mock
    private DeliveryScheduleDao deliveryScheduleDao;

    @InjectMocks
    private DeliveryScheduleService deliveryScheduleService;

    private Subscription mockSubscription;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockSubscription = new Subscription(); // Initialize as needed
    }

    @Test
    void testGetDeliveryScheduleById_Success() throws DeliveryScheduleNotFoundException {
        // Arrange
        DeliverySchedule expectedSchedule = new DeliverySchedule(1, mockSubscription, LocalDate.of(2024, 8, 24));
        when(deliveryScheduleDao.findById(1)).thenReturn(expectedSchedule);

        // Act
        DeliverySchedule actualSchedule = deliveryScheduleService.getDeliveryScheduleById(1);

        // Assert
        assertEquals(expectedSchedule, actualSchedule);
    }

    @Test
    void testGetDeliveryScheduleById_Failure() throws DeliveryScheduleNotFoundException {
        // Arrange
        when(deliveryScheduleDao.findById(1)).thenThrow(new DeliveryScheduleNotFoundException("Schedule not found"));

        // Act & Assert
        assertThrows(DeliveryScheduleNotFoundException.class, () -> deliveryScheduleService.getDeliveryScheduleById(1));
    }

    @Test
    void testGetAllDeliverySchedules() {
        // Arrange
        List<DeliverySchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.add(new DeliverySchedule(1, mockSubscription, LocalDate.of(2024, 8, 24)));
        expectedSchedules.add(new DeliverySchedule(2, mockSubscription, LocalDate.of(2024, 8, 25)));
        when(deliveryScheduleDao.findAllSchedules()).thenReturn(expectedSchedules);

        // Act
        List<DeliverySchedule> actualSchedules = deliveryScheduleService.getAllDeliverySchedules();

        // Assert
        assertEquals(expectedSchedules, actualSchedules);
    }

    @Test
    void testCreateDeliverySchedule() {
        // Arrange
        DeliverySchedule schedule = new DeliverySchedule(1, mockSubscription, LocalDate.of(2024, 8, 24));

        // Act
        deliveryScheduleService.createDeliverySchedule(schedule);

        // Assert
        verify(deliveryScheduleDao, times(1)).addDeliverySchedule(schedule);
    }

    @Test
    void testUpdateDeliverySchedule_Success() throws DeliveryScheduleNotFoundException {
        // Arrange
        DeliverySchedule schedule = new DeliverySchedule(1, mockSubscription, LocalDate.of(2024, 8, 24));

        // Act
        deliveryScheduleService.updateDeliverySchedule(schedule);

        // Assert
        verify(deliveryScheduleDao, times(1)).updateDeliverySchedule(schedule);
    }

    @Test
    void testUpdateDeliverySchedule_Failure() throws DeliveryScheduleNotFoundException {
        // Arrange
        DeliverySchedule schedule = new DeliverySchedule(1, mockSubscription, LocalDate.of(2024, 8, 24));
        doThrow(new DeliveryScheduleNotFoundException("Schedule not found")).when(deliveryScheduleDao).updateDeliverySchedule(any(DeliverySchedule.class));

        // Act & Assert
        assertThrows(DeliveryScheduleNotFoundException.class, () -> deliveryScheduleService.updateDeliverySchedule(schedule));
    }

    @Test
    void testDeleteDeliverySchedule_Success() throws DeliveryScheduleNotFoundException {
        // Act
        deliveryScheduleService.deleteDeliverySchedule(1);

        // Assert
        verify(deliveryScheduleDao, times(1)).deleteDeliverySchedule(1);
    }

    @Test
    void testDeleteDeliverySchedule_Failure() throws DeliveryScheduleNotFoundException {
        // Arrange
        doThrow(new DeliveryScheduleNotFoundException("Schedule not found")).when(deliveryScheduleDao).deleteDeliverySchedule(1);

        // Act & Assert
        assertThrows(DeliveryScheduleNotFoundException.class, () -> deliveryScheduleService.deleteDeliverySchedule(1));
    }

    @Test
    void testFindSchedulesByOrder() {
        // Arrange
        List<DeliverySchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.add(new DeliverySchedule(1, mockSubscription, LocalDate.of(2024, 8, 24)));
        when(deliveryScheduleDao.findSchedulesByOrder(101)).thenReturn(expectedSchedules);

        // Act
        List<DeliverySchedule> actualSchedules = deliveryScheduleService.findSchedulesByOrder(101);

        // Assert
        assertEquals(expectedSchedules, actualSchedules);
    }

    @Test
    void testFindSchedulesByDate() {
        // Arrange
        LocalDate deliveryDate = LocalDate.of(2024, 8, 24);
        List<DeliverySchedule> expectedSchedules = new ArrayList<>();
        expectedSchedules.add(new DeliverySchedule(1, mockSubscription, deliveryDate));
        when(deliveryScheduleDao.findSchedulesByDate(deliveryDate)).thenReturn(expectedSchedules);

        // Act
        List<DeliverySchedule> actualSchedules = deliveryScheduleService.findSchedulesByDate(deliveryDate);

        // Assert
        assertEquals(expectedSchedules, actualSchedules);
    }
}

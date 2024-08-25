package com.hsbc.dao;

import com.hsbc.exception.delivery.DeliveryScheduleNotFoundException;
import com.hsbc.model.DeliverySchedule;

import java.time.LocalDate;
import java.util.List;

public interface DeliveryScheduleDao {
    DeliverySchedule findById(int scheduleId) throws DeliveryScheduleNotFoundException;

    List<DeliverySchedule> findAllSchedules();

    void addDeliverySchedule(DeliverySchedule schedule);

    void updateDeliverySchedule(DeliverySchedule schedule) throws DeliveryScheduleNotFoundException;

    void deleteDeliverySchedule(int scheduleId) throws DeliveryScheduleNotFoundException;

    List<DeliverySchedule> findSchedulesByOrder(int orderId);

    List<DeliverySchedule> findSchedulesByDate(LocalDate deliveryDate);
}
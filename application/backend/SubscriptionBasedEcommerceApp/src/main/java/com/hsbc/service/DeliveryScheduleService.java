package com.hsbc.service;

import com.hsbc.dao.DeliveryScheduleDao;
import com.hsbc.exception.DeliveryScheduleNotFoundException;
import com.hsbc.factory.DaoFactory;
import com.hsbc.model.DeliverySchedule;

import java.time.LocalDate;
import java.util.List;

public class DeliveryScheduleService {

    private DeliveryScheduleDao deliveryScheduleDAO;

    public DeliveryScheduleService() {
        this.deliveryScheduleDAO = DaoFactory.getDeliveryScheduleDao();
    }

    public DeliverySchedule getDeliveryScheduleById(int scheduleId) throws DeliveryScheduleNotFoundException {
        return deliveryScheduleDAO.findById(scheduleId);
    }

    public List<DeliverySchedule> getAllDeliverySchedules() {
        return deliveryScheduleDAO.findAllSchedules();
    }

    public void createDeliverySchedule(DeliverySchedule schedule) {
        deliveryScheduleDAO.addDeliverySchedule(schedule);
    }

    public void updateDeliverySchedule(DeliverySchedule schedule) throws DeliveryScheduleNotFoundException {
        deliveryScheduleDAO.updateDeliverySchedule(schedule);
    }

    public void deleteDeliverySchedule(int scheduleId) throws DeliveryScheduleNotFoundException {
        deliveryScheduleDAO.deleteDeliverySchedule(scheduleId);
    }

    public List<DeliverySchedule> findSchedulesByOrder(int orderId) {
        return deliveryScheduleDAO.findSchedulesByOrder(orderId);
    }

    public List<DeliverySchedule> findSchedulesByDate(LocalDate deliveryDate) {
        return deliveryScheduleDAO.findSchedulesByDate(deliveryDate);
    }
}
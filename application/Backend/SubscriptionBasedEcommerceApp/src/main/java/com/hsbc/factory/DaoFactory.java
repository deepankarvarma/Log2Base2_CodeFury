package com.hsbc.factory;

import com.hsbc.dao.*;
import com.hsbc.dao.impl.*;

public class DaoFactory {

    public static CategoryDao getCategoryDao() {
        return new CategoryDaoImpl();
    }

    public static ProductDao getProductDao() {
        return new ProductDaoImpl();
    }

    public static SubscriptionDao getSubscriptionDao() {
        return new SubscriptionDaoImpl();
    }

    public static OrderDao getOrderDao() {
        return new OrderDaoImpl();
    }

    public static OrderItemDao getOrderItemDao() {
        return new OrderItemDaoImpl();
    }

    public static DeliveryScheduleDao getDeliveryScheduleDao() {
        return new DeliveryScheduleDaoImpl();
    }

    public static SubscriptionPlanDao getSubscriptionPlanDao() {
        return new SubscriptionPlanDaoImpl();
    }

    public static UserDao getUserDao() {
        return new UserDaoImpl();
    }
}
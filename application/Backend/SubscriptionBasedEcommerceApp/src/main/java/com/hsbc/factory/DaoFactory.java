package com.hsbc.factory;

import com.hsbc.dao.*;
import com.hsbc.dao.impl.*;

/**
 * DaoFactory is a factory class that provides static methods to create and
 * return instances of various DAO (Data Access Object) implementations.
 * <p>
 * The purpose of this factory is to centralize the creation of DAO objects,
 * allowing the rest of the application to interact with data persistence
 * layers without being concerned with the specific implementation details.
 * <p>
 * This design pattern promotes loose coupling and makes it easier to manage
 * and modify the data access layer, should the underlying implementation
 * need to change. By using this factory, the rest of the application remains
 * insulated from these changes, enhancing maintainability and scalability.
 */
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

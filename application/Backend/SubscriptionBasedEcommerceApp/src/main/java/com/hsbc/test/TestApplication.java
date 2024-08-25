package com.hsbc.test;

import com.hsbc.exception.auth.AuthenticationException;
import com.hsbc.exception.order.InsufficientStockException;
import com.hsbc.exception.order.OrderNotFoundException;
import com.hsbc.exception.user.UserAlreadyExistsException;
import com.hsbc.exception.user.UserNotFoundException;
import com.hsbc.model.*;
import com.hsbc.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

public class TestApplication {

    private static UserService userService;
    private static ProductService productService;
    private static CategoryService categoryService;
    private static SubscriptionService subscriptionService;
    private static SubscriptionPlanService subscriptionPlanService;
    private static OrderService orderService;
    private static OrderItemService orderItemService;
    private static DeliveryScheduleService deliveryScheduleService;

    public static void main(String[] args) {
        initializeServices();
        initializeData();

        // Check and generate orders for today's deliveries
        generateOrdersForToday();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("===================================");
            System.out.println("Welcome to our Subscription Based E-Commerce Platform");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.println("===================================");
            System.out.print("Please select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerUser(scanner);
                    break;
                case 2:
                    loginUser(scanner);
                    break;
                case 3:
                    System.out.println("Exiting the system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void initializeServices() {
        userService = new UserService();
        productService = new ProductService();
        categoryService = new CategoryService();
        subscriptionService = new SubscriptionService();
        subscriptionPlanService = new SubscriptionPlanService();
        orderService = new OrderService();
        orderItemService = new OrderItemService();
        deliveryScheduleService = new DeliveryScheduleService();
    }

    private static void initializeData() {
        try {
            try {
                userService.getUserByEmail("admin@foodsub.com");
                System.out.println("Admin already exists.");
            } catch (UserNotFoundException e) {
                // Admin not found, so create one
                User admin = new User();
                admin.setUserName("Admin");
                admin.setPassword("admin123");
                admin.setEmail("admin@foodsub.com");
                admin.setPhoneNumber("1234567890");
                admin.setAddress("Admin HQ");
                admin.setRegistrationDate(LocalDate.now());
                admin.setRole("ADMIN");  // Setting the role for admin
                userService.registerUser(admin);
                System.out.println("Admin created successfully.");
            }
        } catch (Exception e) {
            System.out.println("Error initializing data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void generateOrdersForToday() {
        try {
            LocalDate today = LocalDate.now();
            List<DeliverySchedule> schedules = deliveryScheduleService.findSchedulesByDate(today);
//            System.out.println(schedules);

            for (DeliverySchedule schedule : schedules) {
                Subscription subscription = schedule.getSubscription();

                // Check if an order already exists for this subscription and date
                Order existingOrder = orderService.findOrderBySubscriptionAndDate(subscription.getSubscriptionId(), today);
                if (existingOrder != null) {
                    continue; // Order already exists, skip creation
                }

                // Create a new order
                Order order = new Order();
                order.setUser(subscription.getUser());
                order.setOrderDate(today);
                order.setTotalPrice(0.0); // Price will be updated after adding items
                order.setDeliveryStatus(Order.DeliveryStatus.PENDING);

                // Add order and get generated order ID
                orderService.createOrder(order);
                if (order.getOrderId() == 0) {
                    throw new RuntimeException("Order ID not generated after adding order.");
                }

                // Create an order item for the subscribed product
                Product product = subscription.getSubscriptionPlan().getProduct();
                if (product == null) {
                    throw new RuntimeException("Product not found for SubscriptionPlan ID: " + subscription.getSubscriptionPlan().getSubscriptionPlanId());
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(1);
                orderItem.setPrice(product.getBasePrice() * (1 - subscription.getSubscriptionPlan().getDiscountRate()));

                orderItemService.createOrderItem(orderItem);

                // Update the order's total price
                order.setTotalPrice(orderItem.getPrice());
                orderService.updateOrder(order);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print the entire stack trace for detailed debugging
            System.out.println("Error generating orders for today: " + e.getMessage());
        }
    }


    private static void registerUser(Scanner scanner) {
        try {
            System.out.println("User Registration:");
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            System.out.print("Enter Phone Number: ");
            String phoneNumber = scanner.nextLine();
            System.out.print("Enter Address: ");
            String address = scanner.nextLine();

            User newUser = new User();
            newUser.setUserName(name);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setAddress(address);
            newUser.setRegistrationDate(LocalDate.now());

            userService.registerUser(newUser);
            System.out.println("User registered successfully!");

        } catch (UserAlreadyExistsException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loginUser(Scanner scanner) {
        try {
            System.out.println("Login:");
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            User user = userService.getUserByEmail(email);

            if (!user.getPassword().equals(password)) {
                throw new AuthenticationException("Invalid password!");
            }

            if ("ADMIN".equals(user.getRole())) {
                showAdminMenu(scanner);
            } else {
                showUserMenu(scanner, user);
            }

        } catch (UserNotFoundException e) {
            System.out.println("User not found: " + e.getMessage());
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void showAdminMenu(Scanner scanner) {
        while (true) {
            System.out.println("===================================");
            System.out.println("Admin Menu");
            System.out.println("1. View All Users");
            System.out.println("2. Manage Products");
            System.out.println("3. Manage Categories");
            System.out.println("4. Manage Subscription Plans");
            System.out.println("5. View Subscriptions");
            System.out.println("6. View Order History");
            System.out.println("7. View All Orders");
            System.out.println("8. Update Order Status");
            System.out.println("9. View Daily Delivery List");
            System.out.println("10. Logout");
            System.out.println("===================================");
            System.out.print("Please select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    manageProducts(scanner);
                    break;
                case 3:
                    manageCategories(scanner);
                    break;
                case 4:
                    manageSubscriptionPlans(scanner);
                    break;
                case 5:
                    viewSubscriptions();
                    break;
                case 6:
                    viewDeliveredOrders();
                    break;
                case 7:
                    viewAllOrders();
                    break;
                case 8:
                    updateOrderStatus(scanner);
                    break;
                case 9:
                    viewDailyDeliveryList();
                    break;
                case 10:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }


    private static void viewAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            System.out.println("List of Users:");
            for (User user : users) {
                System.out.println("ID: " + user.getUserId() + ", Name: " + user.getUserName() + ", Email: " + user.getEmail());
            }
        } catch (Exception e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }
    }


    private static void manageProducts(Scanner scanner) {
        while (true) {
            System.out.println("===================================");
            System.out.println("Manage Products");
            System.out.println("1. View All Products");
            System.out.println("2. Add Product");
            System.out.println("3. Update Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Back to Admin Menu");
            System.out.println("===================================");
            System.out.print("Please select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAllProducts();
                    break;
                case 2:
                    addProduct(scanner);
                    break;
                case 3:
                    updateProduct(scanner);
                    break;
                case 4:
                    deleteProduct(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void viewAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            if (products.isEmpty()) {
                System.out.println("No products available.");
            } else {
                System.out.println("Available Products:");
                for (Product product : products) {
                    System.out.println("ID: " + product.getProductId() + ", Name: " + product.getProductName() +
                            ", Price: " + product.getBasePrice() + ", Stock: " + product.getStock());
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching products: " + e.getMessage());
        }
    }

    private static void addProduct(Scanner scanner) {
        try {
            System.out.print("Enter Product Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Description: ");
            String description = scanner.nextLine();
            System.out.print("Enter Base Price: ");
            double basePrice = scanner.nextDouble();
            scanner.nextLine(); // Consume newline
            System.out.print("Enter Stock Quantity: ");
            int stock = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            System.out.print("Enter Category ID: ");
            int categoryId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Category category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                System.out.println("Category not found.");
                return;
            }

            Product product = new Product();
            product.setProductName(name);
            product.setDescription(description);
            product.setBasePrice(basePrice);
            product.setStock(stock);
            product.setCategory(category);

            productService.createProduct(product);
            System.out.println("Product added successfully!");

        } catch (Exception e) {
            System.out.println("Error adding product: " + e.getMessage());
        }
    }

    private static void updateProduct(Scanner scanner) {
        try {
            System.out.print("Enter Product ID to update: ");
            int productId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Product product = productService.getProductById(productId);
            if (product == null) {
                System.out.println("Product not found.");
                return;
            }

            System.out.print("Enter New Product Name (current: " + product.getProductName() + "): ");
            String name = scanner.nextLine();
            System.out.print("Enter New Description (current: " + product.getDescription() + "): ");
            String description = scanner.nextLine();
            System.out.print("Enter New Base Price (current: " + product.getBasePrice() + "): ");
            double basePrice = scanner.nextDouble();
            scanner.nextLine(); // Consume newline
            System.out.print("Enter New Stock Quantity (current: " + product.getStock() + "): ");
            int stock = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            System.out.print("Enter New Category ID (current: " + product.getCategory().getCategoryId() + "): ");
            int categoryId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Category category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                System.out.println("Category not found.");
                return;
            }

            product.setProductName(name);
            product.setDescription(description);
            product.setBasePrice(basePrice);
            product.setStock(stock);
            product.setCategory(category);

            productService.updateProduct(product);
            System.out.println("Product updated successfully!");

        } catch (Exception e) {
            System.out.println("Error updating product: " + e.getMessage());
        }
    }

    private static void deleteProduct(Scanner scanner) {
        try {
            System.out.print("Enter Product ID to delete: ");
            int productId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            productService.deleteProduct(productId);
            System.out.println("Product deleted successfully!");

        } catch (Exception e) {
            System.out.println("Error deleting product: " + e.getMessage());
        }
    }


    private static void manageCategories(Scanner scanner) {
        while (true) {
            System.out.println("===================================");
            System.out.println("Manage Categories");
            System.out.println("1. View All Categories");
            System.out.println("2. Add Category");
            System.out.println("3. Update Category");
            System.out.println("4. Delete Category");
            System.out.println("5. Back to Admin Menu");
            System.out.println("===================================");
            System.out.print("Please select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAllCategories();
                    break;
                case 2:
                    addCategory(scanner);
                    break;
                case 3:
                    updateCategory(scanner);
                    break;
                case 4:
                    deleteCategory(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void viewAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            if (categories.isEmpty()) {
                System.out.println("No categories available.");
            } else {
                System.out.println("Available Categories:");
                for (Category category : categories) {
                    System.out.println("ID: " + category.getCategoryId() + ", Name: " + category.getCategoryName());
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching categories: " + e.getMessage());
        }
    }

    private static void addCategory(Scanner scanner) {
        try {
            System.out.print("Enter Category Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Description: ");
            String description = scanner.nextLine();

            Category category = new Category();
            category.setCategoryName(name);
            category.setCategoryDescription(description);

            categoryService.createCategory(category);
            System.out.println("Category added successfully!");

        } catch (Exception e) {
            System.out.println("Error adding category: " + e.getMessage());
        }
    }

    private static void updateCategory(Scanner scanner) {
        try {
            System.out.print("Enter Category ID to update: ");
            int categoryId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Category category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                System.out.println("Category not found.");
                return;
            }

            System.out.print("Enter New Category Name (current: " + category.getCategoryName() + "): ");
            String name = scanner.nextLine();
            System.out.print("Enter New Description (current: " + category.getCategoryDescription() + "): ");
            String description = scanner.nextLine();

            category.setCategoryName(name);
            category.setCategoryDescription(description);

            categoryService.updateCategory(category);
            System.out.println("Category updated successfully!");

        } catch (Exception e) {
            System.out.println("Error updating category: " + e.getMessage());
        }
    }

    private static void deleteCategory(Scanner scanner) {
        try {
            System.out.print("Enter Category ID to delete: ");
            int categoryId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            categoryService.deleteCategory(categoryId);
            System.out.println("Category deleted successfully!");

        } catch (Exception e) {
            System.out.println("Error deleting category: " + e.getMessage());
        }
    }

    private static void manageSubscriptionPlans(Scanner scanner) {
        while (true) {
            System.out.println("===================================");
            System.out.println("Manage Subscription Plans");
            System.out.println("1. View All Subscription Plans");
            System.out.println("2. Add Subscription Plan");
            System.out.println("3. Update Subscription Plan");
            System.out.println("4. Activate/Deactivate Status of Subscription Plan");
            System.out.println("5. Back to Admin Menu");
            System.out.println("===================================");
            System.out.print("Please select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewAllSubscriptionPlans();
                    break;
                case 2:
                    addSubscriptionPlan(scanner);
                    break;
                case 3:
                    updateSubscriptionPlan(scanner);
                    break;
                case 4:
                    toggleSubscriptionPlan(scanner);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void viewAllSubscriptionPlans() {
        try {
            List<SubscriptionPlan> plans = subscriptionPlanService.getAllSubscriptionPlans();
            System.out.println("All Subscription Plans:");
            for (SubscriptionPlan plan : plans) {
                System.out.println("Plan ID: " + plan.getSubscriptionPlanId() +
                        ", Product: " + plan.getProduct().getProductName() +
                        ", Type: " + plan.getSubscriptionType() +
                        ", Interval Days: " + plan.getIntervalDays() +
                        ", Discount: " + plan.getDiscountRate() + "%" +
                        ", Active: " + plan.isActive());
            }
        } catch (Exception e) {
            System.out.println("Error fetching subscription plans: " + e.getMessage());
        }
    }

    private static void addSubscriptionPlan(Scanner scanner) {
        try {
            System.out.print("Enter Product ID: ");
            int productId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Product product = productService.getProductById(productId);
            if (product == null) {
                System.out.println("Product not found.");
                return;
            }

            System.out.print("Enter Subscription Type (DAILY, WEEKLY, ALTERNATE_DAY, BI-WEEKLY, MONTHLY): ");
            String subscriptionType = scanner.nextLine().toUpperCase();

            System.out.print("Enter Interval Days: ");
            int intervalDays = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter Discount Rate: ");
            double discountRate = scanner.nextDouble();
            scanner.nextLine(); // Consume newline

            SubscriptionPlan plan = new SubscriptionPlan();
            plan.setProduct(product);
            plan.setSubscriptionType(SubscriptionPlan.SubscriptionType.valueOf(subscriptionType));
            plan.setIntervalDays(intervalDays);
            plan.setDiscountRate(discountRate);
            plan.setActive(true);

            subscriptionPlanService.createSubscriptionPlan(plan);

            System.out.println("Subscription Plan added successfully!");

        } catch (Exception e) {
            System.out.println("Error adding Subscription Plan: " + e.getMessage());
        }
    }

    private static void updateSubscriptionPlan(Scanner scanner) {
        try {
            System.out.print("Enter Subscription Plan ID to update: ");
            int planId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(planId);
            if (plan == null) {
                System.out.println("Subscription Plan not found.");
                return;
            }

            System.out.println("Select new Subscription Type (current: " + plan.getSubscriptionType() + "):");
            System.out.println("1. DAILY");
            System.out.println("2. ALTERNATE_DAY");
            System.out.println("3. WEEKLY");
            System.out.println("4. BI_WEEKLY");
            System.out.println("5. MONTHLY");
            System.out.print("Please select an option: ");
            int typeChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            SubscriptionPlan.SubscriptionType newType;
            int intervalDays;

            switch (typeChoice) {
                case 1 -> {
                    newType = SubscriptionPlan.SubscriptionType.DAILY;
                    intervalDays = 1;
                }
                case 2 -> {
                    newType = SubscriptionPlan.SubscriptionType.ALTERNATE_DAY;
                    intervalDays = 2;
                }
                case 3 -> {
                    newType = SubscriptionPlan.SubscriptionType.WEEKLY;
                    intervalDays = 7;
                }
                case 4 -> {
                    newType = SubscriptionPlan.SubscriptionType.BI_WEEKLY;
                    intervalDays = 14;
                }
                case 5 -> {
                    newType = SubscriptionPlan.SubscriptionType.MONTHLY;
                    intervalDays = 30;
                }
                default -> {
                    System.out.println("Invalid choice. Subscription Type not updated.");
                    return;
                }
            }

            System.out.print("Enter new Discount Rate (current: " + plan.getDiscountRate() + "): ");
            double discountRate = scanner.nextDouble();
            scanner.nextLine(); // Consume newline

            // Set the new values to the plan
            plan.setSubscriptionType(newType);
            plan.setIntervalDays(intervalDays);  // Automatically set based on Subscription Type
            plan.setDiscountRate(discountRate);

            subscriptionPlanService.updateSubscriptionPlan(plan);

            System.out.println("Subscription Plan updated successfully!");

        } catch (Exception e) {
            System.out.println("Error updating Subscription Plan: " + e.getMessage());
        }
    }


    private static void toggleSubscriptionPlan(Scanner scanner) {
        viewAllSubscriptionPlans();  // Show all plans first
        System.out.print("Enter Subscription Plan ID to toggle status: ");
        int planId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(planId);
            if (plan == null) {
                System.out.println("Subscription Plan not found.");
                return;
            }

            if (plan.isActive()) {
                subscriptionPlanService.deactivateSubscriptionPlan(planId);
                System.out.println("Subscription Plan deactivated successfully.");
            } else {
                subscriptionPlanService.activateSubscriptionPlan(planId);
                System.out.println("Subscription Plan activated successfully.");
            }

        } catch (Exception e) {
            System.out.println("Error toggling Subscription Plan status: " + e.getMessage());
        }
    }


    private static void viewSubscriptions() {
        try {
            List<Subscription> subscriptions = subscriptionService.getAllSubscriptions();
            System.out.println("List of Subscriptions:");
            for (Subscription subscription : subscriptions) {
                System.out.println("ID: " + subscription.getSubscriptionId() + ", User: " + subscription.getUser().getUserName() +
                        ", Plan: " + subscription.getSubscriptionPlan().getSubscriptionType() +
                        ", Start Date: " + subscription.getStartDate() + ", End Date: " + subscription.getEndDate() +
                        ", Status: " + subscription.getStatus());
            }
        } catch (Exception e) {
            System.out.println("Error fetching subscriptions: " + e.getMessage());
        }
    }

    private static void viewDeliveredOrders() {
        try {
            List<Order> orders = orderService.findOrdersByStatus(Order.DeliveryStatus.DELIVERED);
            System.out.println("Delivered Orders:");

            if (orders.isEmpty()) {
                System.out.println("No orders have been delivered yet.");
            } else {
                for (Order order : orders) {
                    System.out.println("Order ID: " + order.getOrderId() +
                            ", User: " + order.getUser().getUserName() +
                            ", Date: " + order.getOrderDate() +
                            ", Total Price: " + order.getTotalPrice());
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching delivered orders: " + e.getMessage());
        }
    }

    private static void viewAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            System.out.println("List of All Orders:");
            for (Order order : orders) {
                System.out.println("Order ID: " + order.getOrderId() +
                        ", User: " + order.getUser().getUserName() +
                        ", Date: " + order.getOrderDate() +
                        ", Total Price: " + order.getTotalPrice() +
                        ", Status: " + order.getDeliveryStatus());
            }
        } catch (Exception e) {
            System.out.println("Error fetching orders: " + e.getMessage());
        }
    }

    private static void updateOrderStatus(Scanner scanner) {
        try {
            System.out.print("Enter Order ID to update: ");
            int orderId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            System.out.print("Enter new status (PENDING/DELIVERED/CANCELLED): ");
            String statusInput = scanner.nextLine().toUpperCase();
            Order.DeliveryStatus status = Order.DeliveryStatus.valueOf(statusInput);

            if (status == Order.DeliveryStatus.DELIVERED) {
                orderService.markOrderAsDelivered(orderId);
                System.out.println("Order status updated to DELIVERED and stock updated.");
            } else {
                orderService.updateOrderStatus(orderId, status);
                System.out.println("Order status updated to " + status);
            }
        } catch (OrderNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (InsufficientStockException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status entered. Please enter PENDING, DELIVERED, or CANCELLED.");
        } catch (Exception e) {
            System.out.println("Error updating order status: " + e.getMessage());
        }
    }

    private static void viewDailyDeliveryList() {
        try {
            LocalDate today = LocalDate.now();
            List<Order> orders = orderService.findOrdersByDate(today);
            System.out.println("Today's Delivery List:");
            for (Order order : orders) {
                System.out.println("Order ID: " + order.getOrderId() +
                        ", User: " + order.getUser().getUserName() +
                        ", Product(s): ");
                List<OrderItem> items = orderItemService.findOrderItemsByOrder(order.getOrderId());
                for (OrderItem item : items) {
                    System.out.println("  - " + item.getProduct().getProductName() + " (Quantity: " + item.getQuantity() + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching daily delivery list: " + e.getMessage());
        }
    }


    private static void showUserMenu(Scanner scanner, User user) {
        while (true) {
            System.out.println("===================================");
            System.out.println("User Menu - Welcome " + user.getUserName());
            System.out.println("1. Browse Products");
            System.out.println("2. Subscribe to Product");
            System.out.println("3. View and Manage Subscriptions");
            System.out.println("4. Place One-Time Order");
            System.out.println("5. View Order History");
            System.out.println("6. Logout");
            System.out.println("===================================");
            System.out.print("Please select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    browseProducts();
                    break;
                case 2:
                    subscribeToProduct(scanner, user);
                    break;
                case 3:
                    viewAndManageSubscriptions(scanner, user);
                    break;
                case 4:
                    placeOneTimeOrder(scanner, user);
                    break;
                case 5:
                    viewOrderHistory(user);
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }


    private static void browseProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            System.out.println("Available Products:");
            for (Product product : products) {
                System.out.println("Product ID: " + product.getProductId() + ", Name: " + product.getProductName() +
                        ", Price: " + product.getBasePrice() + ", Description: " + product.getDescription());
            }
        } catch (Exception e) {
            System.out.println("Error fetching products: " + e.getMessage());
        }
    }

    private static void subscribeToProduct(Scanner scanner, User user) {
        try {
            browseProducts();

            System.out.print("Enter Product ID to subscribe: ");
            int productId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Product product = productService.getProductById(productId);
            if (product == null) {
                System.out.println("Product not found.");
                return;
            }

            // Fetch active subscription plans for the selected product
            List<SubscriptionPlan> plans = subscriptionPlanService.findActivePlansByProduct(productId);
            if (plans.isEmpty()) {
                System.out.println("No subscription plans available for this product.");
                System.out.print("Would you like to place a one-time order instead? (yes/no): ");
                String choice = scanner.nextLine().trim().toLowerCase();

                if (choice.equals("yes")) {
                    placeOneTimeOrder(scanner, user, product);
                } else {
                    System.out.println("No action taken. Returning to user menu.");
                }
                return;
            }

            System.out.println("Available Subscription Plans:");
            for (SubscriptionPlan plan : plans) {
                System.out.println("Plan ID: " + plan.getSubscriptionPlanId() + ", Type: " + plan.getSubscriptionType() +
                        ", Interval Days: " + plan.getIntervalDays() + ", Discount: " + plan.getDiscountRate() + "%");
            }

            System.out.print("Enter Subscription Plan ID: ");
            int planId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(planId);
            if (plan == null || plan.getProduct().getProductId() != productId) {
                System.out.println("Invalid Subscription Plan ID for the selected product.");
                return;
            }

            System.out.print("Enter Start Date (yyyy-mm-dd): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine());

            System.out.print("Enter End Date (yyyy-mm-dd): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine());

            Subscription subscription = new Subscription();
            subscription.setUser(user);
            subscription.setSubscriptionPlan(plan);
            subscription.setStartDate(startDate);
            subscription.setEndDate(endDate);
            subscription.setStatus(Subscription.Status.ACTIVE);

            subscriptionService.createSubscription(subscription);

            System.out.println("Subscription added successfully!");

        } catch (Exception e) {
            System.out.println("Error subscribing to product: " + e.getMessage());
        }
    }


    private static void viewAndManageSubscriptions(Scanner scanner, User user) {
        try {
            List<Subscription> subscriptions = subscriptionService.findActiveSubscriptionsByUser(user.getUserId());
            if (subscriptions.isEmpty()) {
                System.out.println("You have no active subscriptions.");
                return;
            }

            System.out.println("Your Subscriptions:");
            for (Subscription subscription : subscriptions) {
                System.out.println("Subscription ID: " + subscription.getSubscriptionId() +
                        ", Product: " + subscription.getSubscriptionPlan().getProduct().getProductName() +
                        ", Plan: " + subscription.getSubscriptionPlan().getSubscriptionType() +
                        ", Start Date: " + subscription.getStartDate() + ", End Date: " + subscription.getEndDate());
            }

            System.out.print("Enter Subscription ID to manage or 0 to go back: ");
            int subscriptionId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (subscriptionId == 0) {
                return;
            }

            Subscription subscription = subscriptionService.getSubscriptionById(subscriptionId);
            if (subscription == null) {
                System.out.println("Subscription not found.");
                return;
            }

            manageSubscription(scanner, subscription);

        } catch (Exception e) {
            System.out.println("Error managing subscriptions: " + e.getMessage());
        }
    }

    private static void manageSubscription(Scanner scanner, Subscription subscription) {
        while (true) {
            System.out.println("===================================");
            System.out.println("Manage Subscription");
            System.out.println("1. View Details");
            System.out.println("2. Cancel Subscription");
            System.out.println("3. Back to User Menu");
            System.out.println("===================================");
            System.out.print("Please select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewSubscriptionDetails(subscription);
                    break;
                case 2:
                    if (confirmCancellation(scanner)) {
                        cancelSubscription(subscription);
                        return; // Exit after cancellation
                    }
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void viewSubscriptionDetails(Subscription subscription) {
        System.out.println("Subscription Details:");
        System.out.println("Product: " + subscription.getSubscriptionPlan().getProduct().getProductName());
        System.out.println("Plan: " + subscription.getSubscriptionPlan().getSubscriptionType());
        System.out.println("Start Date: " + subscription.getStartDate());
        System.out.println("End Date: " + subscription.getEndDate());
        System.out.println("Status: " + subscription.getStatus());
    }

    private static void cancelSubscription(Subscription subscription) {
        try {
            subscriptionService.deactivateSubscription(subscription.getSubscriptionId());
            System.out.println("Subscription cancelled successfully.");
        } catch (Exception e) {
            System.out.println("Error cancelling subscription: " + e.getMessage());
        }
    }

    private static boolean confirmCancellation(Scanner scanner) {
        System.out.print("Are you sure you want to cancel this subscription? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes") || confirmation.equals("y")) {
            return true;
        } else {
            System.out.println("Cancellation aborted.");
            return false;
        }
    }


    private static void viewOrderHistory(User user) {
        try {
            List<Order> orders = orderService.findOrdersByUser(user.getUserId());
            System.out.println("Your Order History:");

            if (orders.isEmpty()) {
                System.out.println("You have not placed any orders yet.");
            } else {
                for (Order order : orders) {
                    System.out.println("===================================");
                    System.out.println("Order ID: " + order.getOrderId());
                    System.out.println("Date: " + order.getOrderDate());
                    System.out.println("Total Price: " + order.getTotalPrice());
                    System.out.println("Status: " + order.getDeliveryStatus());
                    System.out.println("Items:");

                    // Fetch and display the order items
                    List<OrderItem> orderItems = orderItemService.findOrderItemsByOrder(order.getOrderId());
                    for (OrderItem item : orderItems) {
                        System.out.println(" - " + item.getProduct().getProductName() + " (Quantity: " + item.getQuantity() +
                                ", Price: " + item.getPrice() + ")");
                    }
                    System.out.println("===================================");
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching order history: " + e.getMessage());
        }
    }


    private static void placeOneTimeOrder(Scanner scanner, User user) {
        try {
            browseProducts();

            System.out.println("Enter the product IDs you wish to order (comma-separated): ");
            String productIdsInput = scanner.nextLine();
            String[] productIdsArray = productIdsInput.split(",");

            List<OrderItem> orderItems = new ArrayList<>();
            double totalPrice = 0.0;

            for (String productIdStr : productIdsArray) {
                int productId = Integer.parseInt(productIdStr.trim());
                Product product = productService.getProductById(productId);

                if (product == null) {
                    System.out.println("Product ID " + productId + " not found.");
                    return;
                }

                System.out.print("Enter quantity for " + product.getProductName() + ": ");
                int quantity = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(product.getBasePrice() * quantity);

                orderItems.add(orderItem);
                totalPrice += orderItem.getPrice();
            }

            // Create and save the order
            Order order = new Order();
            order.setUser(user);
            order.setOrderDate(LocalDate.now());
            order.setTotalPrice(totalPrice);
            order.setDeliveryStatus(Order.DeliveryStatus.PENDING);

            orderService.createOrder(order);

            // Link the order items to the order
            for (OrderItem orderItem : orderItems) {
                orderItem.setOrder(order);
                orderItemService.createOrderItem(orderItem);
            }

            System.out.println("One-time order placed successfully!");

        } catch (Exception e) {
            System.out.println("Error placing order: " + e.getMessage());
        }
    }

    private static void placeOneTimeOrder(Scanner scanner, User user, Product product) {
        try {
            System.out.print("Enter Quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            Order order = new Order();
            order.setUser(user);
            order.setOrderDate(LocalDate.now());
            order.setTotalPrice(product.getBasePrice() * quantity);
            order.setDeliveryStatus(Order.DeliveryStatus.PENDING);

            orderService.createOrder(order);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getBasePrice() * quantity);

            orderItemService.createOrderItem(orderItem);

            System.out.println("One-time order placed successfully!");

        } catch (Exception e) {
            System.out.println("Error placing one-time order: " + e.getMessage());
        }
    }
}

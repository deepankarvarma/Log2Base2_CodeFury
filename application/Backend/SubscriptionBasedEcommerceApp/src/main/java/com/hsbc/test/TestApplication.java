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

/**
 * TestApplication serves as the entry point for the subscription-based e-commerce platform.
 * It simulates the core functionalities of the system, allowing for user interaction via
 * a console-based interface.
 */
public class TestApplication {

    // Service layer dependencies
    private static UserService userService;
    private static ProductService productService;
    private static CategoryService categoryService;
    private static SubscriptionService subscriptionService;
    private static SubscriptionPlanService subscriptionPlanService;
    private static OrderService orderService;
    private static OrderItemService orderItemService;
    private static DeliveryScheduleService deliveryScheduleService;

    /**
     * Main method that starts the application and presents a menu-driven interface to the user.
     */
    public static void main(String[] args) {
        initializeServices(); // Initialize service instances
        initializeData(); // Set up initial data, including admin user

        // Automatically generate orders for any scheduled deliveries today
        generateOrdersForToday();

        Scanner scanner = new Scanner(System.in);

        // Main application loop
        while (true) {
            // Display menu options to the user
            System.out.println("===================================");
            System.out.println("Welcome to our Subscription Based E-Commerce Platform");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.println("===================================");
            System.out.print("Please select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Handle user menu selection
            switch (choice) {
                case 1:
                    registerUser(scanner); // Handle user registration
                    break;
                case 2:
                    loginUser(scanner); // Handle user login
                    break;
                case 3:
                    System.out.println("Exiting the system. Goodbye!"); // Exit the application
                    return;
                default:
                    System.out.println("Invalid choice! Please try again."); // Handle invalid menu input
            }
        }
    }

    /**
     * Initializes all the service instances required by the application.
     * This method centralizes the service creation, allowing for easier management.
     */
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

    /**
     * Sets up initial data in the system, including the creation of an admin user.
     * If the admin user already exists, the creation process is skipped.
     */
    private static void initializeData() {
        try {
            try {
                // Check if the admin user already exists
                userService.getUserByEmail("admin@foodsub.com");
                //System.out.println("Admin already exists.");
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
                //System.out.println("Admin created successfully.");
            }
        } catch (Exception e) {
            // Handle any exceptions that occur during the initialization
            System.out.println("Error initializing data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Generates orders for today's deliveries based on the delivery schedules.
     * This method is executed at the start of the application to ensure all scheduled
     * deliveries for the current day have corresponding orders.
     */
    private static void generateOrdersForToday() {
        try {
            LocalDate today = LocalDate.now();
            List<DeliverySchedule> schedules = deliveryScheduleService.findSchedulesByDate(today);

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

                // Add the order item
                orderItemService.createOrderItem(orderItem);

                // Update the order's total price
                order.setTotalPrice(orderItem.getPrice());
                orderService.updateOrder(order);
            }
        } catch (Exception e) {
            // Handle exceptions and provide detailed error information
            e.printStackTrace(); // Print the entire stack trace for detailed debugging
            System.out.println("Error generating orders for today: " + e.getMessage());
        }
    }

    /**
     * Handles the registration of a new user by collecting user details and
     * invoking the userService to save the new user to the database.
     */
    private static void registerUser(Scanner scanner) {
        try {
            // Prompt user for registration details
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

            // Create a new User object with the collected details
            User newUser = new User();
            newUser.setUserName(name);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setAddress(address);
            newUser.setRegistrationDate(LocalDate.now());

            // Attempt to register the user
            userService.registerUser(newUser);
            System.out.println("User registered successfully!");

        } catch (UserAlreadyExistsException e) {
            System.out.println(e.getMessage()); // Handle case where the user already exists
        } catch (Exception e) {
            e.printStackTrace(); // Handle any other exceptions
        }
    }

    /**
     * Handles user login by verifying the user's email and password.
     * Depending on the user's role (ADMIN or USER), it directs them to the appropriate menu.
     */
    private static void loginUser(Scanner scanner) {
        try {
            // Prompt user for login credentials
            System.out.println("Login:");
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            // Fetch the user by email
            User user = userService.getUserByEmail(email);

            // Validate the password
            if (!user.getPassword().equals(password)) {
                throw new AuthenticationException("Invalid password!");
            }

            // Redirect user to the appropriate menu based on their role
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

    /**
     * Displays the admin menu and handles admin-specific operations such as
     * managing products, categories, subscription plans, and viewing orders.
     */
    private static void showAdminMenu(Scanner scanner) {
        while (true) {
            // Display admin menu options
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

            // Handle admin menu selection
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

    /**
     * Retrieves and displays a list of all registered users in the system.
     */
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

    /**
     * Displays the product management menu and handles actions related to
     * viewing, adding, updating, and deleting products.
     */
    private static void manageProducts(Scanner scanner) {
        while (true) {
            // Display product management menu options
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

            // Handle product management menu selection
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

    /**
     * Retrieves and displays all products available in the system.
     */
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

    /**
     * Prompts the admin to enter product details and then creates a new product
     * in the system using the productService.
     */
    private static void addProduct(Scanner scanner) {
        try {
            // Prompt admin for product details
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

            // Retrieve the category by ID
            Category category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                System.out.println("Category not found.");
                return;
            }

            // Create a new product with the provided details
            Product product = new Product();
            product.setProductName(name);
            product.setDescription(description);
            product.setBasePrice(basePrice);
            product.setStock(stock);
            product.setCategory(category);

            // Save the product using the productService
            productService.createProduct(product);
            System.out.println("Product added successfully!");

        } catch (Exception e) {
            System.out.println("Error adding product: " + e.getMessage());
        }
    }

    /**
     * Prompts the admin to update the details of an existing product.
     */
    private static void updateProduct(Scanner scanner) {
        try {
            // Prompt admin for the product ID to update
            System.out.print("Enter Product ID to update: ");
            int productId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Fetch the product by ID
            Product product = productService.getProductById(productId);
            if (product == null) {
                System.out.println("Product not found.");
                return;
            }

            // Prompt admin for new details, with current values displayed as default
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

            // Fetch the new category by ID
            Category category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                System.out.println("Category not found.");
                return;
            }

            // Update the product with the new details
            product.setProductName(name);
            product.setDescription(description);
            product.setBasePrice(basePrice);
            product.setStock(stock);
            product.setCategory(category);

            // Save the updated product using the productService
            productService.updateProduct(product);
            System.out.println("Product updated successfully!");

        } catch (Exception e) {
            System.out.println("Error updating product: " + e.getMessage());
        }
    }

    /**
     * Prompts the admin to delete a product from the system based on the product ID.
     */
    private static void deleteProduct(Scanner scanner) {
        try {
            // Prompt admin for the product ID to delete
            System.out.print("Enter Product ID to delete: ");
            int productId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Delete the product using the productService
            productService.deleteProduct(productId);
            System.out.println("Product deleted successfully!");

        } catch (Exception e) {
            System.out.println("Error deleting product: " + e.getMessage());
        }
    }

    /**
     * Manages category-related operations, including viewing, adding, updating,
     * and deleting categories, through a menu-driven interface.
     */
    private static void manageCategories(Scanner scanner) {
        while (true) {
            // Display category management options
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

            // Handle menu selection
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
                    return;  // Return to the admin menu
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    /**
     * Retrieves and displays all categories available in the system.
     */
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

    /**
     * Prompts the admin to enter details for a new category and then creates
     * the category using the categoryService.
     */
    private static void addCategory(Scanner scanner) {
        try {
            System.out.print("Enter Category Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Description: ");
            String description = scanner.nextLine();

            // Create and save the new category
            Category category = new Category();
            category.setCategoryName(name);
            category.setCategoryDescription(description);

            categoryService.createCategory(category);
            System.out.println("Category added successfully!");

        } catch (Exception e) {
            System.out.println("Error adding category: " + e.getMessage());
        }
    }

    /**
     * Prompts the admin to update the details of an existing category.
     */
    private static void updateCategory(Scanner scanner) {
        try {
            System.out.print("Enter Category ID to update: ");
            int categoryId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Retrieve the category by ID
            Category category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                System.out.println("Category not found.");
                return;
            }

            // Prompt for new details
            System.out.print("Enter New Category Name (current: " + category.getCategoryName() + "): ");
            String name = scanner.nextLine();
            System.out.print("Enter New Description (current: " + category.getCategoryDescription() + "): ");
            String description = scanner.nextLine();

            // Update the category
            category.setCategoryName(name);
            category.setCategoryDescription(description);

            categoryService.updateCategory(category);
            System.out.println("Category updated successfully!");

        } catch (Exception e) {
            System.out.println("Error updating category: " + e.getMessage());
        }
    }

    /**
     * Prompts the admin to delete a category from the system based on the category ID.
     */
    private static void deleteCategory(Scanner scanner) {
        try {
            System.out.print("Enter Category ID to delete: ");
            int categoryId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Delete the category
            categoryService.deleteCategory(categoryId);
            System.out.println("Category deleted successfully!");

        } catch (Exception e) {
            System.out.println("Error deleting category: " + e.getMessage());
        }
    }

    /**
     * Manages subscription plan-related operations, including viewing, adding,
     * updating, and toggling the status of subscription plans, through a menu-driven interface.
     */
    private static void manageSubscriptionPlans(Scanner scanner) {
        while (true) {
            // Display subscription plan management options
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
            scanner.nextLine(); // Consume newline

            // Handle menu selection
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
                    return;  // Return to the admin menu
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    /**
     * Retrieves and displays all subscription plans available in the system.
     */
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

    /**
     * Prompts the admin to enter details for a new subscription plan and then
     * creates the plan using the subscriptionPlanService.
     */
    private static void addSubscriptionPlan(Scanner scanner) {
        try {
            // Prompt for product and plan details
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

            // Create and save the new subscription plan
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

    /**
     * Prompts the admin to update the details of an existing subscription plan.
     */
    private static void updateSubscriptionPlan(Scanner scanner) {
        try {
            System.out.print("Enter Subscription Plan ID to update: ");
            int planId = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Retrieve the subscription plan by ID
            SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(planId);
            if (plan == null) {
                System.out.println("Subscription Plan not found.");
                return;
            }

            // Prompt for new subscription type and discount rate
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

            // Determine the interval days based on the selected type
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

            // Prompt for new discount rate
            System.out.print("Enter new Discount Rate (current: " + plan.getDiscountRate() + "): ");
            double discountRate = scanner.nextDouble();
            scanner.nextLine(); // Consume newline

            // Update the plan with the new values
            plan.setSubscriptionType(newType);
            plan.setIntervalDays(intervalDays);
            plan.setDiscountRate(discountRate);

            subscriptionPlanService.updateSubscriptionPlan(plan);
            System.out.println("Subscription Plan updated successfully!");

        } catch (Exception e) {
            System.out.println("Error updating Subscription Plan: " + e.getMessage());
        }
    }

    /**
     * Toggles the active status of a subscription plan based on admin input.
     */
    private static void toggleSubscriptionPlan(Scanner scanner) {
        viewAllSubscriptionPlans();  // Show all plans first
        System.out.print("Enter Subscription Plan ID to toggle status: ");
        int planId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            // Retrieve the subscription plan by ID
            SubscriptionPlan plan = subscriptionPlanService.getSubscriptionPlanById(planId);
            if (plan == null) {
                System.out.println("Subscription Plan not found.");
                return;
            }

            // Toggle the active status
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


    /**
     * Retrieves and displays all subscriptions in the system.
     */
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

    /**
     * Retrieves and displays all delivered orders.
     */
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

    /**
     * Retrieves and displays all orders in the system.
     */
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

    /**
     * Allows the admin to update the status of an order.
     */
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

    /**
     * Retrieves and displays the list of orders scheduled for delivery today.
     */
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

    /**
     * Displays the user menu and handles user-specific actions such as browsing products,
     * subscribing to products, and viewing order history.
     */
    private static void showUserMenu(Scanner scanner, User user) {
        while (true) {
            // Display user-specific menu options
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

            // Handle user menu selection
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
                    return;  // Logout and return to main menu
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    /**
     * Retrieves and displays all products available in the system.
     */
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

    /**
     * Allows the user to subscribe to a product by selecting an active subscription plan and specifying the subscription period.
     * If no subscription plans are available, the user is given an option to place a one-time order instead.
     */
    private static void subscribeToProduct(Scanner scanner, User user) {
        try {
            // Display all available products for the user to choose from
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
                // If no active subscription plans are found, offer the user a one-time order option
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

            // Display available subscription plans for the user to choose from
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

            // Get the subscription period from the user
            System.out.print("Enter Start Date (yyyy-mm-dd): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine());

            System.out.print("Enter End Date (yyyy-mm-dd): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine());

            // Create and save the subscription
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

    /**
     * Allows the user to view and manage their active subscriptions. The user can select a subscription to view details or cancel it.
     */
    private static void viewAndManageSubscriptions(Scanner scanner, User user) {
        try {
            // Fetch and display the user's active subscriptions
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
                return; // Return to user menu
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

    /**
     * Allows the user to manage a specific subscription by viewing details or canceling the subscription.
     */
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
                    return; // Return to user menu
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    /**
     * Displays the details of a specific subscription.
     */
    private static void viewSubscriptionDetails(Subscription subscription) {
        System.out.println("Subscription Details:");
        System.out.println("Product: " + subscription.getSubscriptionPlan().getProduct().getProductName());
        System.out.println("Plan: " + subscription.getSubscriptionPlan().getSubscriptionType());
        System.out.println("Start Date: " + subscription.getStartDate());
        System.out.println("End Date: " + subscription.getEndDate());
        System.out.println("Status: " + subscription.getStatus());
    }

    /**
     * Cancels a specific subscription, changing its status to inactive.
     */
    private static void cancelSubscription(Subscription subscription) {
        try {
            subscriptionService.deactivateSubscription(subscription.getSubscriptionId());
            System.out.println("Subscription cancelled successfully.");
        } catch (Exception e) {
            System.out.println("Error cancelling subscription: " + e.getMessage());
        }
    }

    /**
     * Confirms whether the user really wants to cancel the subscription.
     *
     * @return true if the user confirms, false otherwise.
     */
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

    /**
     * Displays the user's order history, including details of each order and the items within those orders.
     */
    private static void viewOrderHistory(User user) {
        try {
            // Fetch and display all orders placed by the user
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

                    // Fetch and display the order items associated with each order
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

    /**
     * Allows the user to place a one-time order by selecting multiple products and specifying the quantity for each.
     * The total price is calculated and the order is saved along with the order items.
     */
    private static void placeOneTimeOrder(Scanner scanner, User user) {
        try {
            // Display all available products for the user to choose from
            browseProducts();

            System.out.println("Enter the product IDs you wish to order (comma-separated): ");
            String productIdsInput = scanner.nextLine();
            String[] productIdsArray = productIdsInput.split(",");

            List<OrderItem> orderItems = new ArrayList<>();
            double totalPrice = 0.0;

            // Loop through each selected product and collect order details
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

                // Create an order item and calculate the price
                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(product.getBasePrice() * quantity);

                orderItems.add(orderItem);
                totalPrice += orderItem.getPrice(); // Add to the total order price
            }

            // Create and save the order
            Order order = new Order();
            order.setUser(user);
            order.setOrderDate(LocalDate.now());
            order.setTotalPrice(totalPrice);
            order.setDeliveryStatus(Order.DeliveryStatus.PENDING);

            orderService.createOrder(order);

            // Link the order items to the order and save them
            for (OrderItem orderItem : orderItems) {
                orderItem.setOrder(order);
                orderItemService.createOrderItem(orderItem);
            }

            System.out.println("One-time order placed successfully!");

        } catch (Exception e) {
            System.out.println("Error placing order: " + e.getMessage());
        }
    }

    /**
     * Overloaded method to place a one-time order for a specific product. This method is used when the user
     * selects a product to subscribe but there are no subscription plans available for that product, then he/she
     * is directed to this function if he chooses to place-one-time order for that product.
     */
    private static void placeOneTimeOrder(Scanner scanner, User user, Product product) {
        try {
            System.out.print("Enter Quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Create and save the order
            Order order = new Order();
            order.setUser(user);
            order.setOrderDate(LocalDate.now());
            order.setTotalPrice(product.getBasePrice() * quantity);
            order.setDeliveryStatus(Order.DeliveryStatus.PENDING);

            orderService.createOrder(order);

            // Create and save the order item
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import auth.AuthMiddleware;
import auth.AuthResponse;
import auth.AuthService;
import database.MongoDBConnection;

public class AuthApp {
    private static String currentToken = null;
    private static final AuthService authService = new AuthService();
    private static final AuthMiddleware authMiddleware = new AuthMiddleware();
    private static final Cart userCart = new Cart();
    
    // Product catalog
    private static final String[][] PRODUCTS = {
        {"Rice (Basmati)", "15"},
        {"Wheat Flour", "8"},
        {"Pulses (Dal)", "12"},
        {"Cooking Oil", "10"},
        {"Sugar", "6"},
        {"Salt", "2"},
        {"Potato Chips", "3"},
        {"Cookies", "5"},
        {"Chocolates", "8"},
        {"Coffee", "15"},
        {"Tea", "10"},
        {"Soft Drinks", "2"},
        {"Detergent Powder", "12"},
        {"Dish Soap", "4"},
        {"Floor Cleaner", "6"},
        {"Disinfectant Spray", "8"},
        {"Refrigerator", "800"},
        {"Washing Machine", "600"},
        {"Microwave Oven", "200"},
        {"Air Conditioner", "1200"},
        {"Water Purifier", "300"},
        {"Smartphone", "500"},
        {"Laptop", "1000"},
        {"Bluetooth Speaker", "80"},
        {"Headphones", "50"},
        {"Smart Watch", "250"},
        {"Tablet", "400"},
        {"Shampoo", "8"},
        {"Soap", "3"},
        {"Toothpaste", "4"},
        {"Face Cream", "15"},
        {"Body Lotion", "12"}
    };
    
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            
            while (running) {
                if (currentToken == null) {
                    showPublicMenu(scanner);
                } else {
                    Document user = authMiddleware.getCurrentUser(currentToken);
                    if (user != null) {
                        String role = user.getString("role");
                        if ("admin".equals(role)) {
                            showAdminMenu(scanner);
                        } else {
                            showUserMenu(scanner);
                        }
                    } else {
                        currentToken = null;
                    }
                }
            }
            
            MongoDBConnection.close();
        }
    }
    
    private static void showPublicMenu(Scanner scanner) {
        System.out.println("\n========================================");
        System.out.println("   SUPERKART AUTHENTICATION SYSTEM");
        System.out.println("========================================");
        System.out.println("1. User Registration");
        System.out.println("2. User Login");
        System.out.println("3. Admin Login");
        System.out.println("4. Create Admin");
        System.out.println("5. Exit");
        System.out.println("========================================");
        System.out.print("Enter your choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch (choice) {
            case 1:
                userRegistration(scanner);
                break;
            case 2:
                userLogin(scanner);
                break;
            case 3:
                adminLogin(scanner);
                break;
            case 4:
                createAdmin(scanner);
                break;
            case 5:
                System.out.println("\nThank you for using Superkart!");
                System.exit(0);
                break;
            default:
                System.out.println("\n[ERROR] Invalid choice!");
        }
    }
    
    private static void showUserMenu(Scanner scanner) {
        System.out.println("\n========================================");
        System.out.println("        USER DASHBOARD");
        System.out.println("========================================");
        System.out.println("1. View Profile");
        System.out.println("2. Browse Products");
        System.out.println("3. View Cart");
        System.out.println("4. Quick Add (Multiple Products)");
        System.out.println("5. My Orders");
        System.out.println("6. Logout");
        System.out.println("========================================");
        System.out.print("Enter your choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch (choice) {
            case 1:
                viewProfile();
                break;
            case 2:
                browseProducts(scanner);
                break;
            case 3:
                viewCart(scanner);
                break;
            case 4:
                quickAddProducts(scanner);
                break;
            case 5:
                viewMyOrders();
                break;
            case 6:
                logout();
                break;
            default:
                System.out.println("\n[ERROR] Invalid choice!");
        }
    }
    
    private static void showAdminMenu(Scanner scanner) {
        System.out.println("\n========================================");
        System.out.println("        ADMIN DASHBOARD");
        System.out.println("========================================");
        System.out.println("1. View Profile");
        System.out.println("2. Manage Users");
        System.out.println("3. Manage Products");
        System.out.println("4. View All Orders");
        System.out.println("5. System Settings");
        System.out.println("6. Logout");
        System.out.println("========================================");
        System.out.print("Enter your choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch (choice) {
            case 1:
                viewProfile();
                break;
            case 2:
                manageUsers(scanner);
                break;
            case 3:
                manageProducts(scanner);
                break;
            case 4:
                viewAllOrders();
                break;
            case 5:
                systemSettings();
                break;
            case 6:
                logout();
                break;
            default:
                System.out.println("\n[ERROR] Invalid choice!");
        }
    }
    
    private static void userRegistration(Scanner scanner) {
        System.out.println("\n--- USER REGISTRATION ---");
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password (min 8 characters): ");
        String password = scanner.nextLine();
        System.out.print("Confirm Password: ");
        String confirmPassword = scanner.nextLine();
        
        if (!password.equals(confirmPassword)) {
            System.out.println("\n[ERROR] Passwords do not match!");
            return;
        }
        
        AuthResponse response = authService.registerUser(name, email, password);
        System.out.println("\n" + (response.isSuccess() ? "[SUCCESS]" : "[ERROR]") + " " + response.getMessage());
        
        if (response.isSuccess()) {
            currentToken = response.getAccessToken();
            System.out.println("Access Token: " + response.getAccessToken().substring(0, 20) + "...");
            System.out.println("Refresh Token: " + response.getRefreshToken().substring(0, 20) + "...");
        }
    }
    
    private static void userLogin(Scanner scanner) {
        System.out.println("\n--- USER LOGIN ---");
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        
        AuthResponse response = authService.loginUser(email, password);
        System.out.println("\n" + (response.isSuccess() ? "[SUCCESS]" : "[ERROR]") + " " + response.getMessage());
        
        if (response.isSuccess()) {
            currentToken = response.getAccessToken();
            System.out.println("Access Token: " + response.getAccessToken().substring(0, 20) + "...");
        }
    }
    
    private static void adminLogin(Scanner scanner) {
        System.out.println("\n--- ADMIN LOGIN ---");
        System.out.print("Enter Admin Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine();
        
        AuthResponse response = authService.loginAdmin(email, password);
        System.out.println("\n" + (response.isSuccess() ? "[SUCCESS]" : "[ERROR]") + " " + response.getMessage());
        
        if (response.isSuccess()) {
            currentToken = response.getAccessToken();
            System.out.println("Access Token: " + response.getAccessToken().substring(0, 20) + "...");
        }
    }
    
    private static void createAdmin(Scanner scanner) {
        System.out.println("\n--- CREATE ADMIN (SETUP ONLY) ---");
        System.out.print("Enter Admin Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Admin Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine();
        
        boolean success = authService.createAdmin(name, email, password);
        if (success) {
            System.out.println("\n[SUCCESS] Admin created successfully!");
        } else {
            System.out.println("\n[ERROR] Admin already exists or creation failed!");
        }
    }
    
    private static void viewProfile() {
        if (!authMiddleware.requireAuth(currentToken)) {
            return;
        }
        
        Document user = authMiddleware.getCurrentUser(currentToken);
        if (user != null) {
            System.out.println("\n--- YOUR PROFILE ---");
            System.out.println("Name: " + user.getString("name"));
            System.out.println("Email: " + user.getString("email"));
            System.out.println("Role: " + user.getString("role").toUpperCase());
            System.out.println("Member Since: " + new java.util.Date(user.getLong("createdAt")));
        }
    }
    
    private static void logout() {
        currentToken = null;
        System.out.println("\n[SUCCESS] Logged out successfully!");
    }
    
    private static void browseProducts(Scanner scanner) {
        System.out.println("\n========================================");
        System.out.println("        PRODUCT CATALOG");
        System.out.println("========================================");
        System.out.println("NOTE: Add ONE product at a time here.");
        System.out.println("For multiple products, use 'Quick Add' from menu.");
        System.out.println("========================================");
        System.out.println("\n--- GROCERIES ---");
        System.out.println("1. Rice (Basmati) - $15/kg");
        System.out.println("2. Wheat Flour - $8/kg");
        System.out.println("3. Pulses (Dal) - $12/kg");
        System.out.println("4. Cooking Oil - $10/liter");
        System.out.println("5. Sugar - $6/kg");
        System.out.println("6. Salt - $2/kg");
        
        System.out.println("\n--- SNACKS & BEVERAGES ---");
        System.out.println("7. Potato Chips - $3/pack");
        System.out.println("8. Cookies - $5/pack");
        System.out.println("9. Chocolates - $8/box");
        System.out.println("10. Coffee - $15/jar");
        System.out.println("11. Tea - $10/box");
        System.out.println("12. Soft Drinks - $2/bottle");
        
        System.out.println("\n--- CLEANING SUPPLIES ---");
        System.out.println("13. Detergent Powder - $12/kg");
        System.out.println("14. Dish Soap - $4/bottle");
        System.out.println("15. Floor Cleaner - $6/bottle");
        System.out.println("16. Disinfectant Spray - $8/bottle");
        
        System.out.println("\n--- HOME APPLIANCES ---");
        System.out.println("17. Refrigerator - $800");
        System.out.println("18. Washing Machine - $600");
        System.out.println("19. Microwave Oven - $200");
        System.out.println("20. Air Conditioner - $1200");
        System.out.println("21. Water Purifier - $300");
        
        System.out.println("\n--- ELECTRONICS ---");
        System.out.println("22. Smartphone - $500");
        System.out.println("23. Laptop - $1000");
        System.out.println("24. Bluetooth Speaker - $80");
        System.out.println("25. Headphones - $50");
        System.out.println("26. Smart Watch - $250");
        System.out.println("27. Tablet - $400");
        
        System.out.println("\n--- PERSONAL CARE ---");
        System.out.println("28. Shampoo - $8/bottle");
        System.out.println("29. Soap - $3/pack");
        System.out.println("30. Toothpaste - $4/tube");
        System.out.println("31. Face Cream - $15/jar");
        System.out.println("32. Body Lotion - $12/bottle");
        System.out.println("\n========================================");
        System.out.print("Enter product number to add to cart (0 to go back): ");
        
        try {
            int productChoice = scanner.nextInt();
            scanner.nextLine();
            
            if (productChoice > 0 && productChoice <= 32) {
                System.out.print("Enter quantity: ");
                int quantity = scanner.nextInt();
                scanner.nextLine();
                
                String productName = PRODUCTS[productChoice - 1][0];
                double price = Double.parseDouble(PRODUCTS[productChoice - 1][1]);
                
                userCart.addItem(productName, price, quantity);
                System.out.println("\n[SUCCESS] Added " + quantity + " x " + productName + " to cart!");
                System.out.println("Cart Total: $" + String.format("%.2f", userCart.getTotalAmount()));
                System.out.println("Total Items in Cart: " + userCart.getTotalItems());
                
                System.out.print("\nAdd more products? (y/n): ");
                String addMore = scanner.nextLine();
                if (addMore.equalsIgnoreCase("y")) {
                    browseProducts(scanner);
                }
            } else if (productChoice != 0) {
                System.out.println("\n[ERROR] Invalid product number!");
            }
        } catch (java.util.InputMismatchException e) {
            scanner.nextLine(); // Clear invalid input
            System.out.println("\n[ERROR] Invalid input! Please enter a single product number.");
            System.out.println("\n========================================");
            System.out.println("TIP: To add multiple products at once:");
            System.out.println("1. Go back to User Dashboard");
            System.out.println("2. Select 'Quick Add (Multiple Products)'");
            System.out.println("3. Enter products like: 1,2 then 4,3 then done");
            System.out.println("========================================");
        }
    }
    
    private static void viewMyOrders() {
        System.out.println("\n========================================");
        System.out.println("        MY ORDERS");
        System.out.println("========================================");
        
        Document user = authMiddleware.getCurrentUser(currentToken);
        if (user == null) {
            System.out.println("\n[ERROR] User not found!");
            return;
        }
        
        String userEmail = user.getString("email");
        MongoCollection<Document> ordersCollection = MongoDBConnection.getDatabase().getCollection("orders");
        
        try (MongoCursor<Document> cursor = ordersCollection.find(new Document("userEmail", userEmail)).iterator()) {
            if (!cursor.hasNext()) {
                System.out.println("\nNo orders found!");
            } else {
                while (cursor.hasNext()) {
                    Document order = cursor.next();
                    System.out.println("\nOrder ID: " + order.getString("orderId"));
                    System.out.println("Total: $" + String.format("%.2f", order.getDouble("totalAmount")));
                    System.out.println("Payment: " + order.getString("paymentMethod"));
                    System.out.println("Address: " + order.getString("deliveryAddress"));
                    System.out.println("Phone: " + order.getString("phone"));
                    System.out.println("Date: " + new Date(order.getLong("orderDate")));
                    System.out.println("Status: " + order.getString("status"));
                    
                    @SuppressWarnings("unchecked")
                    List<Document> items = (List<Document>) order.get("items");
                    System.out.println("Items:");
                    for (Document item : items) {
                        System.out.println("  - " + item.getString("productName") + 
                                         " x" + item.getInteger("quantity") + 
                                         " = $" + String.format("%.2f", item.getDouble("totalPrice")));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("\n[ERROR] Failed to fetch orders: " + e.getMessage());
        }
        
        System.out.println("\n========================================");
    }
    
    private static void manageUsers(Scanner scanner) {
        System.out.println("\n========================================");
        System.out.println("        USER MANAGEMENT");
        System.out.println("========================================");
        System.out.println("1. View All Users");
        System.out.println("2. Search User");
        System.out.println("3. Delete User");
        System.out.println("4. Back");
        System.out.print("Enter choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch (choice) {
            case 1:
                System.out.println("\n--- ALL USERS ---");
                MongoCollection<Document> usersCollection = MongoDBConnection.getDatabase().getCollection("users");
                
                try (MongoCursor<Document> cursor = usersCollection.find().iterator()) {
                    int count = 1;
                    if (!cursor.hasNext()) {
                        System.out.println("No users found!");
                    } else {
                        while (cursor.hasNext()) {
                            Document user = cursor.next();
                            System.out.println(count + ". " + user.getString("email") + 
                                             " - " + user.getString("name") + 
                                             " (" + user.getString("role").toUpperCase() + ")");
                            count++;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[ERROR] Failed to fetch users: " + e.getMessage());
                }
                break;
                
            case 2:
                System.out.print("Enter email to search: ");
                String email = scanner.nextLine();
                
                MongoCollection<Document> usersCollection2 = MongoDBConnection.getDatabase().getCollection("users");
                Document user = usersCollection2.find(new Document("email", email)).first();
                
                if (user != null) {
                    System.out.println("\n--- USER FOUND ---");
                    System.out.println("Name: " + user.getString("name"));
                    System.out.println("Email: " + user.getString("email"));
                    System.out.println("Role: " + user.getString("role").toUpperCase());
                    System.out.println("Created: " + new Date(user.getLong("createdAt")));
                } else {
                    System.out.println("\n[ERROR] User not found!");
                }
                break;
                
            case 3:
                System.out.print("Enter email to delete: ");
                String emailToDelete = scanner.nextLine();
                System.out.print("Are you sure? (yes/no): ");
                String confirm = scanner.nextLine();
                
                if (confirm.equalsIgnoreCase("yes")) {
                    MongoCollection<Document> usersCollection3 = MongoDBConnection.getDatabase().getCollection("users");
                    long deleted = usersCollection3.deleteOne(new Document("email", emailToDelete)).getDeletedCount();
                    
                    if (deleted > 0) {
                        System.out.println("\n[SUCCESS] User deleted successfully!");
                    } else {
                        System.out.println("\n[ERROR] User not found!");
                    }
                }
                break;
        }
    }
    
    private static void manageProducts(Scanner scanner) {
        System.out.println("\n========================================");
        System.out.println("        PRODUCT MANAGEMENT");
        System.out.println("========================================");
        System.out.println("1. Add New Product");
        System.out.println("2. Update Product");
        System.out.println("3. Delete Product");
        System.out.println("4. View All Products");
        System.out.println("5. Back");
        System.out.print("Enter choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        MongoCollection<Document> productsCollection = MongoDBConnection.getDatabase().getCollection("products");
        
        switch (choice) {
            case 1:
                System.out.print("\nEnter Product Name: ");
                String name = scanner.nextLine();
                System.out.print("Enter Price: $");
                double price = scanner.nextDouble();
                scanner.nextLine();
                System.out.print("Enter Category: ");
                String category = scanner.nextLine();
                
                Document product = new Document("name", name)
                        .append("price", price)
                        .append("category", category)
                        .append("createdAt", System.currentTimeMillis());
                
                try {
                    productsCollection.insertOne(product);
                    System.out.println("\n[SUCCESS] Product '" + name + "' added successfully!");
                    System.out.println("Price: $" + price + " | Category: " + category);
                } catch (Exception e) {
                    System.out.println("\n[ERROR] Failed to add product: " + e.getMessage());
                }
                break;
                
            case 4:
                browseProducts(scanner);
                break;
        }
    }
    
    private static void viewAllOrders() {
        System.out.println("\n========================================");
        System.out.println("        ALL ORDERS (ADMIN)");
        System.out.println("========================================");
        
        MongoCollection<Document> ordersCollection = MongoDBConnection.getDatabase().getCollection("orders");
        
        try (MongoCursor<Document> cursor = ordersCollection.find().iterator()) {
            if (!cursor.hasNext()) {
                System.out.println("\nNo orders found!");
            } else {
                while (cursor.hasNext()) {
                    Document order = cursor.next();
                    System.out.println("\nOrder ID: " + order.getString("orderId") + 
                                     " - " + order.getString("userEmail"));
                    System.out.println("Total: $" + String.format("%.2f", order.getDouble("totalAmount")) + 
                                     " | Status: " + order.getString("status"));
                    System.out.println("Payment: " + order.getString("paymentMethod"));
                    System.out.println("Date: " + new Date(order.getLong("orderDate")));
                    
                    @SuppressWarnings("unchecked")
                    List<Document> items = (List<Document>) order.get("items");
                    System.out.print("Items: ");
                    for (int i = 0; i < items.size(); i++) {
                        Document item = items.get(i);
                        System.out.print(item.getString("productName"));
                        if (i < items.size() - 1) System.out.print(", ");
                    }
                    System.out.println();
                }
            }
        } catch (Exception e) {
            System.out.println("\n[ERROR] Failed to fetch orders: " + e.getMessage());
        }
        
        System.out.println("\n========================================");
    }
    
    private static void systemSettings() {
        System.out.println("\n========================================");
        System.out.println("        SYSTEM SETTINGS");
        System.out.println("========================================");
        
        try {
            MongoCollection<Document> usersCollection = MongoDBConnection.getDatabase().getCollection("users");
            MongoCollection<Document> ordersCollection = MongoDBConnection.getDatabase().getCollection("orders");
            MongoCollection<Document> productsCollection = MongoDBConnection.getDatabase().getCollection("products");
            
            long totalUsers = usersCollection.countDocuments();
            long totalOrders = ordersCollection.countDocuments();
            long totalProducts = productsCollection.countDocuments();
            
            System.out.println("1. Database Status: Connected");
            System.out.println("2. Total Users: " + totalUsers);
            System.out.println("3. Total Products: " + totalProducts + " (32 in catalog)");
            System.out.println("4. Total Orders: " + totalOrders);
            System.out.println("5. Server Status: Running");
        } catch (Exception e) {
            System.out.println("1. Database Status: Error - " + e.getMessage());
            System.out.println("2. Total Users: N/A");
            System.out.println("3. Total Products: N/A");
            System.out.println("4. Total Orders: N/A");
            System.out.println("5. Server Status: Running");
        }
        
        System.out.println("========================================");
    }
    
    private static void viewCart(Scanner scanner) {
        System.out.println("\n========================================");
        System.out.println("        YOUR SHOPPING CART");
        System.out.println("========================================");
        
        if (userCart.isEmpty()) {
            System.out.println("\nYour cart is empty!");
            System.out.println("========================================");
            return;
        }
        
        List<CartItem> items = userCart.getItems();
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            System.out.println((i + 1) + ". " + item.getProductName());
            System.out.println("   Price: $" + item.getPrice() + " x " + item.getQuantity() + " = $" + String.format("%.2f", item.getTotalPrice()));
        }
        
        System.out.println("\n----------------------------------------");
        System.out.println("Total Items: " + userCart.getTotalItems());
        System.out.println("Total Amount: $" + String.format("%.2f", userCart.getTotalAmount()));
        System.out.println("========================================");
        
        System.out.println("\n1. Proceed to Checkout");
        System.out.println("2. Remove Item");
        System.out.println("3. Clear Cart");
        System.out.println("4. Continue Shopping");
        System.out.print("Enter choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch (choice) {
            case 1:
                checkout(scanner);
                break;
            case 2:
                System.out.print("Enter item number to remove: ");
                int itemNum = scanner.nextInt();
                scanner.nextLine();
                if (itemNum > 0 && itemNum <= items.size()) {
                    userCart.removeItem(itemNum - 1);
                    System.out.println("\n[SUCCESS] Item removed from cart!");
                }
                break;
            case 3:
                userCart.clearCart();
                System.out.println("\n[SUCCESS] Cart cleared!");
                break;
            case 4:
                browseProducts(scanner);
                break;
        }
    }
    
    private static void checkout(Scanner scanner) {
        if (userCart.isEmpty()) {
            System.out.println("\n[ERROR] Your cart is empty!");
            return;
        }
        
        Document user = authMiddleware.getCurrentUser(currentToken);
        if (user == null) {
            System.out.println("\n[ERROR] User not found!");
            return;
        }
        
        System.out.println("\n========================================");
        System.out.println("        CHECKOUT");
        System.out.println("========================================");
        
        System.out.print("Enter Delivery Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();
        
        System.out.println("\n--- ORDER SUMMARY ---");
        List<CartItem> items = userCart.getItems();
        for (CartItem item : items) {
            System.out.println(item.getProductName() + " x" + item.getQuantity() + " = $" + String.format("%.2f", item.getTotalPrice()));
        }
        System.out.println("\nTotal Amount: $" + String.format("%.2f", userCart.getTotalAmount()));
        System.out.println("Delivery Address: " + address);
        System.out.println("Phone: " + phone);
        
        System.out.println("\n--- PAYMENT METHOD ---");
        System.out.println("1. Cash on Delivery");
        System.out.println("2. UPI");
        System.out.println("3. Card");
        System.out.print("Select payment method: ");
        
        int paymentMethod = scanner.nextInt();
        scanner.nextLine();
        
        String orderId = "ORD" + System.currentTimeMillis();
        String paymentMethodStr = paymentMethod == 1 ? "Cash on Delivery" : paymentMethod == 2 ? "UPI" : "Card";
        
        // Save order to MongoDB
        try {
            MongoCollection<Document> ordersCollection = MongoDBConnection.getDatabase().getCollection("orders");
            
            List<Document> orderItems = new ArrayList<>();
            for (CartItem item : items) {
                Document orderItem = new Document("productName", item.getProductName())
                        .append("price", item.getPrice())
                        .append("quantity", item.getQuantity())
                        .append("totalPrice", item.getTotalPrice());
                orderItems.add(orderItem);
            }
            
            Document order = new Document("orderId", orderId)
                    .append("userEmail", user.getString("email"))
                    .append("userName", user.getString("name"))
                    .append("items", orderItems)
                    .append("totalAmount", userCart.getTotalAmount())
                    .append("deliveryAddress", address)
                    .append("phone", phone)
                    .append("paymentMethod", paymentMethodStr)
                    .append("status", "Processing")
                    .append("orderDate", System.currentTimeMillis());
            
            ordersCollection.insertOne(order);
            
            System.out.println("\n========================================");
            System.out.println("[SUCCESS] Order Placed Successfully!");
            System.out.println("========================================");
            System.out.println("Order ID: " + orderId);
            System.out.println("Total Amount: $" + String.format("%.2f", userCart.getTotalAmount()));
            System.out.println("Payment Method: " + paymentMethodStr);
            System.out.println("Delivery Address: " + address);
            System.out.println("Estimated Delivery: 3-5 business days");
            System.out.println("========================================");
            
            // Clear cart after successful order
            userCart.clearCart();
            
        } catch (Exception e) {
            System.out.println("\n[ERROR] Failed to place order: " + e.getMessage());
            System.out.println("Please try again or contact support.");
        }
    }
    
    private static void quickAddProducts(Scanner scanner) {
        System.out.println("\n========================================");
        System.out.println("        QUICK ADD PRODUCTS");
        System.out.println("========================================");
        System.out.println("Add multiple products quickly!");
        System.out.println("Format: product_number,quantity");
        System.out.println("Example: 1,2 (adds 2 units of product 1)");
        System.out.println("Enter 'done' when finished");
        System.out.println("========================================\n");
        
        boolean adding = true;
        while (adding) {
            System.out.print("Enter product (number,quantity) or 'done': ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("done")) {
                adding = false;
                if (!userCart.isEmpty()) {
                    System.out.println("\n[SUCCESS] Added " + userCart.getTotalItems() + " items to cart!");
                    System.out.println("Cart Total: $" + String.format("%.2f", userCart.getTotalAmount()));
                    
                    System.out.print("\nProceed to checkout? (y/n): ");
                    String checkout = scanner.nextLine();
                    if (checkout.equalsIgnoreCase("y")) {
                        viewCart(scanner);
                    }
                }
            } else {
                try {
                    String[] parts = input.split(",");
                    if (parts.length == 2) {
                        int productNum = Integer.parseInt(parts[0].trim());
                        int quantity = Integer.parseInt(parts[1].trim());
                        
                        if (productNum > 0 && productNum <= 32 && quantity > 0) {
                            String productName = PRODUCTS[productNum - 1][0];
                            double price = Double.parseDouble(PRODUCTS[productNum - 1][1]);
                            
                            userCart.addItem(productName, price, quantity);
                            System.out.println("[SUCCESS] Added " + quantity + " x " + productName);
                        } else {
                            System.out.println("[ERROR] Invalid product number or quantity!");
                        }
                    } else {
                        System.out.println("[ERROR] Invalid format! Use: number,quantity");
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("[ERROR] Invalid input! Use format: number,quantity");
                }
            }
        }
    }
}

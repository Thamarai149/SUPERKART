import java.util.List;
import java.util.Scanner;

import org.bson.Document;

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
            System.out.println("\n[ERROR] Invalid input! Please enter a single number.");
            System.out.println("Tip: For multiple products, use 'Quick Add' from the main menu!");
        }
    }
    
    private static void viewMyOrders() {
        System.out.println("\n========================================");
        System.out.println("        MY ORDERS");
        System.out.println("========================================");
        System.out.println("\nOrder #ORD001");
        System.out.println("Items: Rice, Cooking Oil, Chips");
        System.out.println("Total: $35");
        System.out.println("Status: Delivered");
        System.out.println("Date: 2026-02-20");
        
        System.out.println("\nOrder #ORD002");
        System.out.println("Items: Laptop, Headphones");
        System.out.println("Total: $1050");
        System.out.println("Status: In Transit");
        System.out.println("Date: 2026-02-24");
        
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
        
        if (choice == 1) {
            System.out.println("\n--- ALL USERS ---");
            System.out.println("1. john@example.com - John Doe (User)");
            System.out.println("2. jane@example.com - Jane Smith (User)");
            System.out.println("3. admin@superkart.com - Admin User (Admin)");
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
        
        if (choice == 1) {
            System.out.print("\nEnter Product Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Price: $");
            double price = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Enter Category: ");
            String category = scanner.nextLine();
            System.out.println("\n[SUCCESS] Product '" + name + "' added successfully!");
            System.out.println("Price: $" + price + " | Category: " + category);
        } else if (choice == 4) {
            browseProducts(scanner);
        }
    }
    
    private static void viewAllOrders() {
        System.out.println("\n========================================");
        System.out.println("        ALL ORDERS (ADMIN)");
        System.out.println("========================================");
        System.out.println("\nOrder #ORD001 - john@example.com");
        System.out.println("Items: Rice, Cooking Oil, Chips");
        System.out.println("Total: $35 | Status: Delivered");
        
        System.out.println("\nOrder #ORD002 - jane@example.com");
        System.out.println("Items: Laptop, Headphones");
        System.out.println("Total: $1050 | Status: In Transit");
        
        System.out.println("\nOrder #ORD003 - john@example.com");
        System.out.println("Items: Smartphone, Smart Watch");
        System.out.println("Total: $750 | Status: Processing");
        
        System.out.println("\n========================================");
    }
    
    private static void systemSettings() {
        System.out.println("\n========================================");
        System.out.println("        SYSTEM SETTINGS");
        System.out.println("========================================");
        System.out.println("1. Database Status: Connected");
        System.out.println("2. Total Users: 15");
        System.out.println("3. Total Products: 32");
        System.out.println("4. Total Orders: 48");
        System.out.println("5. Server Status: Running");
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
        
        System.out.println("\n========================================");
        System.out.println("[SUCCESS] Order Placed Successfully!");
        System.out.println("========================================");
        System.out.println("Order ID: " + orderId);
        System.out.println("Total Amount: $" + String.format("%.2f", userCart.getTotalAmount()));
        System.out.println("Payment Method: " + (paymentMethod == 1 ? "Cash on Delivery" : paymentMethod == 2 ? "UPI" : "Card"));
        System.out.println("Delivery Address: " + address);
        System.out.println("Estimated Delivery: 3-5 business days");
        System.out.println("========================================");
        
        // Clear cart after successful order
        userCart.clearCart();
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

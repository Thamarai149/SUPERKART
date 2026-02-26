import java.util.Scanner;

import org.bson.Document;

import auth.AuthMiddleware;
import auth.AuthResponse;
import auth.AuthService;
import database.MongoDBConnection;

public class AuthApp {
    private static String currentToken = null;
    private static AuthService authService = new AuthService();
    private static AuthMiddleware authMiddleware = new AuthMiddleware();
    
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
        System.out.println("4. Create Admin (Setup Only)");
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
                System.out.println("\n✗ Invalid choice!");
        }
    }
    
    private static void showUserMenu(Scanner scanner) {
        System.out.println("\n========================================");
        System.out.println("        USER DASHBOARD");
        System.out.println("========================================");
        System.out.println("1. View Profile");
        System.out.println("2. Browse Products");
        System.out.println("3. My Orders");
        System.out.println("4. Logout");
        System.out.println("========================================");
        System.out.print("Enter your choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch (choice) {
            case 1:
                viewProfile();
                break;
            case 2:
                System.out.println("\n✓ Browsing products...");
                break;
            case 3:
                System.out.println("\n✓ Viewing your orders...");
                break;
            case 4:
                logout();
                break;
            default:
                System.out.println("\n✗ Invalid choice!");
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
                System.out.println("\n✓ Managing users...");
                break;
            case 3:
                System.out.println("\n✓ Managing products...");
                break;
            case 4:
                System.out.println("\n✓ Viewing all orders...");
                break;
            case 5:
                System.out.println("\n✓ System settings...");
                break;
            case 6:
                logout();
                break;
            default:
                System.out.println("\n✗ Invalid choice!");
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
            System.out.println("\n✗ Passwords do not match!");
            return;
        }
        
        AuthResponse response = authService.registerUser(name, email, password);
        System.out.println("\n" + (response.isSuccess() ? "✓" : "✗") + " " + response.getMessage());
        
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
        System.out.println("\n" + (response.isSuccess() ? "✓" : "✗") + " " + response.getMessage());
        
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
        System.out.println("\n" + (response.isSuccess() ? "✓" : "✗") + " " + response.getMessage());
        
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
            System.out.println("\n✓ Admin created successfully!");
        } else {
            System.out.println("\n✗ Admin already exists or creation failed!");
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
        System.out.println("\n✓ Logged out successfully!");
    }
}

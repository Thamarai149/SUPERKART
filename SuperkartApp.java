import java.util.Scanner;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

class LoginService {
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;
    
    public LoginService() {
        this.database = MongoDBConnection.getDatabase();
        this.collection = database.getCollection("profiles");
    }
    
    public boolean authenticate(String email, String password) {
        Document query = new Document("Email", email).append("Password", password);
        Document user = collection.find(query).first();
        
        if (user != null) {
            System.out.println("\n✓ Login Successful!");
            System.out.println("Welcome, " + user.getString("Name"));
            return true;
        } else {
            System.out.println("\n✗ Login Failed!");
            System.out.println("Invalid email or password.");
            return false;
        }
    }
}

public class SuperkartApp {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            
            while (running) {
                System.out.println("\n========================================");
                System.out.println("     SUPERKART E-COMMERCE SYSTEM");
                System.out.println("========================================");
                System.out.println("1. User Registration & Login");
                System.out.println("2. Browse Inventory");
                System.out.println("3. Place Order");
                System.out.println("4. Make Payment");
                System.out.println("5. View All Data");
                System.out.println("6. Exit");
                System.out.println("========================================");
                System.out.print("Enter your choice: ");
                
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                switch (choice) {
                    case 1:
                        handleUserManagement(scanner);
                        break;
                    case 2:
                        handleInventory(scanner);
                        break;
                    case 3:
                        handleOrders(scanner);
                        break;
                    case 4:
                        handlePayments(scanner);
                        break;
                    case 5:
                        viewAllData();
                        break;
                    case 6:
                        running = false;
                        System.out.println("\nThank you for using Superkart!");
                        break;
                    default:
                        System.out.println("\n✗ Invalid choice! Please try again.");
                }
            }
            
            MongoDBConnection.close();
        }
    }
    
    private static void handleUserManagement(Scanner scanner) {
        System.out.println("\n--- USER MANAGEMENT ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.print("Choose option: ");
        int option = scanner.nextInt();
        scanner.nextLine();
        
        if (option == 1) {
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();
            System.out.print("Enter Phone: ");
            String phone = scanner.nextLine();
            
            profile user = new profile(name, email, password, phone);
            user.saveToDatabase();
            
            System.out.println("\n✓ Registration Successful!");
            System.out.println("Name: " + user.Name);
            System.out.println("Email: " + user.Email);
        } else if (option == 2) {
            LoginService loginService = new LoginService();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();
            
            loginService.authenticate(email, password);
        }
    }
    
    private static void handleInventory(Scanner scanner) {
        System.out.println("\n--- INVENTORY MANAGEMENT ---");
        System.out.println("1. Add Inventory");
        System.out.println("2. View Inventory");
        System.out.print("Choose option: ");
        int option = scanner.nextInt();
        scanner.nextLine();
        
        if (option == 1) {
            System.out.print("Enter Grocery Items: ");
            String grocery = scanner.nextLine();
            System.out.print("Enter Snacks Items: ");
            String snacks = scanner.nextLine();
            System.out.print("Enter Cleaning Items: ");
            String cleaning = scanner.nextLine();
            System.out.print("Enter Home Appliances: ");
            String appliances = scanner.nextLine();
            System.out.print("Enter Electronics: ");
            String electronics = scanner.nextLine();
            
            inventoryservices inv = new inventoryservices(grocery, snacks, cleaning, appliances, electronics);
            inv.saveToDatabase();
            
            System.out.println("\n✓ Inventory Added Successfully!");
        } else if (option == 2) {
            System.out.println("\n--- AVAILABLE INVENTORY ---");
            System.out.println("Grocery: Rice, Wheat, Pulses, Oil");
            System.out.println("Snacks: Chips, Cookies, Chocolates");
            System.out.println("Cleaning: Detergents, Disinfectants");
            System.out.println("Appliances: Refrigerators, Washing Machines");
            System.out.println("Electronics: Smartphones, Speakers, Laptops");
        }
    }
    
    private static void handleOrders(Scanner scanner) {
        System.out.println("\n--- ORDER MANAGEMENT ---");
        System.out.print("Enter Order ID: ");
        String orderId = scanner.nextLine();
        System.out.print("Enter Cart Items (comma separated): ");
        String cartItems = scanner.nextLine();
        System.out.print("Enter Order Amount: $");
        String amount = scanner.nextLine();
        System.out.print("Enter Delivery Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter Delivery Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter Product Count: ");
        int count = scanner.nextInt();
        scanner.nextLine();
        
        String deliveryInfo = date + ", " + address;
        String orderHistory = "New Order";
        
        orderservices order = new orderservices(orderId, cartItems, "$" + amount, orderHistory, deliveryInfo, count);
        order.saveToDatabase();
        
        System.out.println("\n✓ Order Placed Successfully!");
        System.out.println("Order ID: " + order.OrderID);
        System.out.println("Total Amount: " + order.orderAmount);
        System.out.println("Delivery: " + order.DeliveryDatePlace);
    }
    
    private static void handlePayments(Scanner scanner) {
        System.out.println("\n--- PAYMENT PROCESSING ---");
        System.out.println("1. Pay with UPI");
        System.out.println("2. Pay with Card");
        System.out.print("Choose payment method: ");
        int method = scanner.nextInt();
        scanner.nextLine();
        
        String upi = "";
        String card = "";
        
        if (method == 1) {
            System.out.print("Enter UPI ID: ");
            upi = scanner.nextLine();
            card = "N/A";
        } else if (method == 2) {
            System.out.print("Enter Card Number: ");
            card = scanner.nextLine();
            upi = "N/A";
        }
        
        System.out.print("Enter Amount: $");
        int amount = scanner.nextInt();
        scanner.nextLine();
        
        String paymentBill = "SUPERKART PAYMENT RECEIPT\n" +
                           "========================\n" +
                           "Payment Method: " + (method == 1 ? "UPI" : "Card") + "\n" +
                           "UPI ID: " + upi + "\n" +
                           "Card: " + card + "\n" +
                           "Amount Paid: $" + amount + "\n" +
                           "Status: SUCCESS\n" +
                           "========================";
        
        payment pay = new payment(upi, card, amount, paymentBill);
        pay.saveToDatabase();
        
        System.out.println("\n✓ Payment Successful!");
        System.out.println("\n" + paymentBill);
    }
    
    private static void viewAllData() {
        System.out.println("\n========================================");
        System.out.println("     ALL DATA SAVED TO MONGODB");
        System.out.println("========================================");
        System.out.println("Database: superkart");
        System.out.println("Collections:");
        System.out.println("  - profiles (User accounts)");
        System.out.println("  - inventory (Product catalog)");
        System.out.println("  - orders (Customer orders)");
        System.out.println("  - payments (Transaction records)");
        System.out.println("\nAll data is stored in MongoDB!");
        System.out.println("========================================");
    }
}

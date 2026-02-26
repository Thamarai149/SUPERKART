package auth;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import database.MongoDBConnection;

public class AuthService {
    private final MongoDatabase database;
    private final MongoCollection<Document> usersCollection;
    
    public AuthService() {
        this.database = MongoDBConnection.getDatabase();
        this.usersCollection = database.getCollection("users");
    }
    
    // Register User
    public AuthResponse registerUser(String name, String email, String password) {
        // Validate input
        if (!ValidationUtil.isValidEmail(email)) {
            return new AuthResponse(false, "Invalid email format", null, null);
        }
        
        if (!ValidationUtil.isValidPassword(password)) {
            return new AuthResponse(false, "Password must be at least 8 characters", null, null);
        }
        
        // Check if user already exists
        Document existingUser = usersCollection.find(new Document("email", email)).first();
        if (existingUser != null) {
            return new AuthResponse(false, "Email already registered", null, null);
        }
        
        // Hash password
        String hashedPassword = PasswordUtil.hashPassword(password);
        
        // Create user document
        Document userDoc = new Document("name", name)
                .append("email", email)
                .append("password", hashedPassword)
                .append("role", "user")
                .append("createdAt", System.currentTimeMillis());
        
        usersCollection.insertOne(userDoc);
        
        // Generate tokens
        String accessToken = JWTUtil.generateAccessToken(email, "user");
        String refreshToken = JWTUtil.generateRefreshToken(email);
        
        return new AuthResponse(true, "Registration successful", accessToken, refreshToken);
    }
    
    // Login User
    public AuthResponse loginUser(String email, String password) {
        // Find user
        Document userDoc = usersCollection.find(new Document("email", email)).first();
        
        if (userDoc == null) {
            return new AuthResponse(false, "Invalid email or password", null, null);
        }
        
        // Check if user role is "user"
        if (!"user".equals(userDoc.getString("role"))) {
            return new AuthResponse(false, "Access denied. Please use admin login.", null, null);
        }
        
        // Verify password
        String hashedPassword = userDoc.getString("password");
        if (!PasswordUtil.verifyPassword(password, hashedPassword)) {
            return new AuthResponse(false, "Invalid email or password", null, null);
        }
        
        // Generate tokens
        String accessToken = JWTUtil.generateAccessToken(email, "user");
        String refreshToken = JWTUtil.generateRefreshToken(email);
        
        return new AuthResponse(true, "Login successful. Welcome " + userDoc.getString("name"), accessToken, refreshToken);
    }
    
    // Login Admin
    public AuthResponse loginAdmin(String email, String password) {
        // Find admin
        Document adminDoc = usersCollection.find(
            new Document("email", email).append("role", "admin")
        ).first();
        
        if (adminDoc == null) {
            return new AuthResponse(false, "Invalid admin credentials", null, null);
        }
        
        // Verify password
        String hashedPassword = adminDoc.getString("password");
        if (!PasswordUtil.verifyPassword(password, hashedPassword)) {
            return new AuthResponse(false, "Invalid admin credentials", null, null);
        }
        
        // Generate tokens
        String accessToken = JWTUtil.generateAccessToken(email, "admin");
        String refreshToken = JWTUtil.generateRefreshToken(email);
        
        return new AuthResponse(true, "Admin login successful. Welcome " + adminDoc.getString("name"), accessToken, refreshToken);
    }
    
    // Create Admin (Manual - for initial setup)
    public boolean createAdmin(String name, String email, String password) {
        // Check if admin already exists
        Document existingAdmin = usersCollection.find(
            new Document("email", email).append("role", "admin")
        ).first();
        
        if (existingAdmin != null) {
            return false;
        }
        
        // Hash password
        String hashedPassword = PasswordUtil.hashPassword(password);
        
        // Create admin document
        Document adminDoc = new Document("name", name)
                .append("email", email)
                .append("password", hashedPassword)
                .append("role", "admin")
                .append("createdAt", System.currentTimeMillis());
        
        usersCollection.insertOne(adminDoc);
        return true;
    }
    
    // Refresh Access Token
    public AuthResponse refreshAccessToken(String refreshToken) {
        if (!JWTUtil.validateToken(refreshToken)) {
            return new AuthResponse(false, "Invalid or expired refresh token", null, null);
        }
        
        String email = JWTUtil.extractEmail(refreshToken);
        Document userDoc = usersCollection.find(new Document("email", email)).first();
        
        if (userDoc == null) {
            return new AuthResponse(false, "User not found", null, null);
        }
        
        String role = userDoc.getString("role");
        String newAccessToken = JWTUtil.generateAccessToken(email, role);
        
        return new AuthResponse(true, "Token refreshed", newAccessToken, refreshToken);
    }
    
    // Verify Token and Get User Info
    public Document verifyAndGetUser(String token) {
        if (!JWTUtil.validateToken(token)) {
            return null;
        }
        
        String email = JWTUtil.extractEmail(token);
        return usersCollection.find(new Document("email", email)).first();
    }
}

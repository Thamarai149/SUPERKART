package auth;

import org.bson.Document;

public class AuthMiddleware {
    private final AuthService authService;
    
    public AuthMiddleware() {
        this.authService = new AuthService();
    }
    
    // Verify if user is authenticated
    public boolean isAuthenticated(String token) {
        return JWTUtil.validateToken(token) && !JWTUtil.isTokenExpired(token);
    }
    
    // Verify if user is admin
    public boolean isAdmin(String token) {
        if (!isAuthenticated(token)) {
            return false;
        }
        String role = JWTUtil.extractRole(token);
        return "admin".equals(role);
    }
    
    // Verify if user is regular user
    public boolean isUser(String token) {
        if (!isAuthenticated(token)) {
            return false;
        }
        String role = JWTUtil.extractRole(token);
        return "user".equals(role);
    }
    
    // Get current user from token
    public Document getCurrentUser(String token) {
        if (!isAuthenticated(token)) {
            return null;
        }
        return authService.verifyAndGetUser(token);
    }
    
    // Block unauthorized access
    public boolean requireAuth(String token) {
        if (!isAuthenticated(token)) {
            System.out.println("[ERROR] Unauthorized: Please login first");
            return false;
        }
        return true;
    }
    
    // Block non-admin access
    public boolean requireAdmin(String token) {
        if (!isAuthenticated(token)) {
            System.out.println("[ERROR] Unauthorized: Please login first");
            return false;
        }
        if (!isAdmin(token)) {
            System.out.println("[ERROR] Forbidden: Admin access required");
            return false;
        }
        return true;
    }
    
    // Block non-user access
    public boolean requireUser(String token) {
        if (!isAuthenticated(token)) {
            System.out.println("[ERROR] Unauthorized: Please login first");
            return false;
        }
        if (!isUser(token)) {
            System.out.println("[ERROR] Forbidden: User access required");
            return false;
        }
        return true;
    }
}

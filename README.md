# Superkart E-Commerce System with Authentication

A complete e-commerce management system with secure authentication, role-based access control, and MongoDB integration.

## Features

### Authentication System
- Separate Admin and User login modules
- Password hashing with BCrypt
- JWT token-based authentication
- Role-based access control (Admin/User)
- Token expiration handling
- Refresh token mechanism
- Input validation and error handling
- Secure logout functionality

### User Features
- User registration with validation
- User login with JWT tokens
- User dashboard access
- Protected user routes
- Profile management

### Admin Features
- Admin login (no public registration)
- Admin dashboard with full access
- User management capabilities
- Product and order management
- System settings access
- Protected admin routes

### Security
- Passwords hashed with BCrypt (12 rounds)
- JWT tokens with expiration (1 hour access, 24 hours refresh)
- Email validation
- Password strength validation (min 8 characters)
- Role-based middleware protection
- Unauthorized access blocking

## Setup

1. Install MongoDB and ensure it's running on `localhost:27017`
2. Install Maven dependencies:
```bash
mvn clean install
```

## Compile

```bash
mvn clean compile
```

## Run

### Authentication Application (Main)
```powershell
mvn exec:java
```

Or specify explicitly:
```powershell
mvn exec:java "-Dexec.mainClass=AuthApp"
```

### Other Applications
```powershell
mvn exec:java "-Dexec.mainClass=SuperkartApp"
mvn exec:java "-Dexec.mainClass=profiles"
mvn exec:java "-Dexec.mainClass=orders"
mvn exec:java "-Dexec.mainClass=inventory"
mvn exec:java "-Dexec.mainClass=payments"
```

## Database Structure

Database Name: `superkart`

### Collections:

**users** - Authentication and user management
- name (String)
- email (String, unique)
- password (String, hashed with BCrypt)
- role (String: "admin" or "user")
- createdAt (Long, timestamp)

**profiles** - User profiles
**inventory** - Product catalog
**orders** - Customer orders
**payments** - Payment transactions

## Authentication Flow

### User Registration & Login
1. User registers with name, email, password
2. Password is hashed with BCrypt
3. User stored in database with role "user"
4. JWT access token (1 hour) and refresh token (24 hours) generated
5. User can access user dashboard and protected routes

### Admin Login
1. Admin credentials verified (admin must be created manually in database)
2. Password verified with BCrypt
3. JWT tokens generated with role "admin"
4. Admin can access admin dashboard and all admin routes
5. Admin cannot access user-only routes

### Token Management
- Access tokens expire in 1 hour
- Refresh tokens expire in 24 hours
- Tokens contain email and role claims
- Middleware validates tokens before granting access

## Creating First Admin

Run the application and select option 4 "Create Admin (Setup Only)":
```
Enter Admin Name: Admin User
Enter Admin Email: admin@superkart.com
Enter Admin Password: SecurePass123
```

## Project Structure

```
SUPERKART/
├── auth/
│   ├── AuthService.java          # Authentication logic
│   ├── AuthMiddleware.java       # Route protection
│   ├── JWTUtil.java              # JWT token management
│   ├── PasswordUtil.java         # Password hashing
│   ├── ValidationUtil.java       # Input validation
│   ├── User.java                 # User model
│   └── AuthResponse.java         # Response wrapper
├── AuthApp.java                  # Main authentication app
├── SuperkartApp.java             # E-commerce app
├── MongoDBConnection.java        # Database connection
├── profiles.java                 # Profile service
├── orders.java                   # Order service
├── inventory.java                # Inventory service
├── payments.java                 # Payment service
├── pom.xml                       # Maven configuration
├── .env                          # Environment variables
└── README.md                     # Documentation
```

## Security Best Practices

1. Passwords are never stored in plain text
2. BCrypt with 12 salt rounds for password hashing
3. JWT tokens with short expiration times
4. Role-based access control enforced
5. Input validation on all user inputs
6. Email format validation
7. Password strength requirements
8. Secure token storage and transmission

## API Endpoints (Conceptual)

### Public Routes
- POST /register - User registration
- POST /login/user - User login
- POST /login/admin - Admin login
- POST /refresh - Refresh access token

### Protected User Routes (Requires user token)
- GET /user/profile - View profile
- GET /user/orders - View orders
- POST /user/order - Place order

### Protected Admin Routes (Requires admin token)
- GET /admin/users - View all users
- GET /admin/orders - View all orders
- POST /admin/product - Add product
- PUT /admin/product/:id - Update product
- DELETE /admin/product/:id - Delete product

## Note

Always use Maven commands to compile and run. Do not use `javac` directly as it won't include dependencies (MongoDB, BCrypt, JWT).

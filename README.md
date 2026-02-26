# Superkart E-Commerce System

A complete e-commerce management system with MongoDB integration for user management, inventory, orders, and payments.

## Features

- User Registration & Login
- Inventory Management
- Order Processing
- Payment Processing (UPI & Card)
- MongoDB Database Integration

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

### Main Application (Interactive Menu)
```powershell
mvn exec:java "-Dexec.mainClass=SuperkartApp"
```

### Individual Services
```powershell
mvn exec:java "-Dexec.mainClass=Main"
mvn exec:java "-Dexec.mainClass=login"
mvn exec:java "-Dexec.mainClass=profiles"
mvn exec:java "-Dexec.mainClass=orders"
mvn exec:java "-Dexec.mainClass=inventory"
mvn exec:java "-Dexec.mainClass=payments"
```

## Database

Database Name: `superkart`

Collections:
- `profiles` - User accounts with authentication
- `inventory` - Product catalog
- `orders` - Customer orders with delivery info
- `payments` - Payment transactions and receipts

## Application Flow

1. Register/Login users
2. Browse and manage inventory
3. Place orders with delivery details
4. Process payments via UPI or Card
5. All data automatically saved to MongoDB

## Note

Always use Maven commands to compile and run. Do not use `javac` directly as it won't include MongoDB dependencies.

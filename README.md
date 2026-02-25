# Superkart MongoDB Project

## Setup

1. Install MongoDB and ensure it's running on `localhost:27017`
2. Install Maven dependencies:
```bash
mvn clean install
```

## Compile

To compile all Java files together (required for MongoDBConnection to be found):

```bash
javac -cp ".;lib/*" *.java
```

Or use Maven:
```bash
mvn compile
```

## Run

After compiling, run any of the main classes:

```bash
java -cp ".;lib/*" profiles
java -cp ".;lib/*" orders
java -cp ".;lib/*" inventory
java -cp ".;lib/*" payments
```

## Database

All data will be saved to the `superkart` database in MongoDB with collections:
- profiles
- orders
- inventory
- payments

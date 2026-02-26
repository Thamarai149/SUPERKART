import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import database.MongoDBConnection;

class payment {
   String Upi;
   String Card;
   int Amount;
   String PaymentBill;

    public payment(String upi, String card, int amount, String paymentBill) {
         this.Upi = upi;
         this.Card = card;
         this.Amount = amount;
         this.PaymentBill = paymentBill;
    }
    
    public void saveToDatabase() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        MongoCollection<Document> collection = database.getCollection("payments");
        
        Document doc = new Document("Upi", this.Upi)
                .append("Card", this.Card)
                .append("Amount", this.Amount)
                .append("PaymentBill", this.PaymentBill);
        
        collection.insertOne(doc);
        System.out.println("Payment saved to MongoDB");
    }
}

public class payments {
    public static void main(String[] args) {
        payment payment1 = new payment("thamaraiselvan@upi", "1234-5678-9012-3456", 50, "Payment for Order ORD12345");
        System.out.println("Payment Details:");
        System.out.println("UPI: " + payment1.Upi);
        System.out.println("Card: " + payment1.Card);
        System.out.println("Amount: $" + payment1.Amount);
        System.out.println("Payment Bill: " + payment1.PaymentBill);
        
        payment1.saveToDatabase();
        MongoDBConnection.close();
    }
}

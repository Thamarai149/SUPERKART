import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

class orderservices {
    String OrderID;
    String CartItems;
    String orderAmount;
    String OrderHistory;            
    String DeliveryDatePlace;
    int DeliveryProductCount;

    public orderservices(String orderID, String cartItems, String orderAmount, String orderHistory, String deliveryDatePlace, int deliveryProductCount) {
        this.OrderID = orderID;
        this.CartItems = cartItems;
        this.orderAmount = orderAmount;
        this.OrderHistory = orderHistory;
        this.DeliveryDatePlace = deliveryDatePlace;
        this.DeliveryProductCount = deliveryProductCount;
    }
    
    public void saveToDatabase() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        MongoCollection<Document> collection = database.getCollection("orders");
        
        Document doc = new Document("OrderID", this.OrderID)
                .append("CartItems", this.CartItems)
                .append("orderAmount", this.orderAmount)
                .append("OrderHistory", this.OrderHistory)
                .append("DeliveryDatePlace", this.DeliveryDatePlace)
                .append("DeliveryProductCount", this.DeliveryProductCount);
        
        collection.insertOne(doc);
        System.out.println("Order saved to MongoDB");
    }
}

public class orders {
    public static void main(String[] args) {
        orderservices order = new orderservices("ORD12345", "Rice, Chips, Detergents", "$50", "Previous orders: ORD11111, ORD22222", "2024-07-01, 123 Main St", 3);
        System.out.println("Order Services:");
        System.out.println("Order ID: " + order.OrderID);
        System.out.println("Cart Items: " + order.CartItems);
        System.out.println("Order Amount: " + order.orderAmount);
        System.out.println("Order History: " + order.OrderHistory);
        System.out.println("Delivery Date & Place: " + order.DeliveryDatePlace);
        System.out.println("Delivery Product Count: " + order.DeliveryProductCount);
        
        order.saveToDatabase();
        MongoDBConnection.close();
    }
}

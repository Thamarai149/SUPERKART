import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import database.MongoDBConnection;

class inventoryservices {
    String GroceryItems;
    String SnacksItems;
    String CleaningItems;
    String HomeAppliancesItems;
    String MobilesSpeakersItems;

    public inventoryservices(String groceryItems, String snacksItems, String cleaningItems, String homeAppliancesItems, String mobilesSpeakersItems) {
        this.GroceryItems = groceryItems;
        this.SnacksItems = snacksItems;
        this.CleaningItems = cleaningItems;
        this.HomeAppliancesItems = homeAppliancesItems;
        this.MobilesSpeakersItems = mobilesSpeakersItems;
    }
    
    public void saveToDatabase() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        MongoCollection<Document> collection = database.getCollection("inventory");
        
        Document doc = new Document("GroceryItems", this.GroceryItems)
                .append("SnacksItems", this.SnacksItems)
                .append("CleaningItems", this.CleaningItems)
                .append("HomeAppliancesItems", this.HomeAppliancesItems)
                .append("MobilesSpeakersItems", this.MobilesSpeakersItems);
        
        collection.insertOne(doc);
        System.out.println("Inventory saved to MongoDB");
    }
}

public class inventory {
    public static void main(String[] args) {
        inventoryservices inventory = new inventoryservices("Rice, Wheat, Pulses", "Chips, Cookies, Chocolates", "Detergents, Disinfectants", "Refrigerators, Washing Machines", "Smartphones, Bluetooth Speakers");
        System.out.println("Inventory Services:");
        System.out.println("Grocery Items: " + inventory.GroceryItems);
        System.out.println("Snacks Items: " + inventory.SnacksItems);
        System.out.println("Cleaning Items: " + inventory.CleaningItems);
        System.out.println("Home Appliances Items: " + inventory.HomeAppliancesItems);
        System.out.println("Mobiles & Speakers Items: " + inventory.MobilesSpeakersItems);
        
        inventory.saveToDatabase();
        MongoDBConnection.close();
    }
}


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

class profile{
    String Name;
    String Email;
    String Password;
    String PhoneNo;
    
    public profile(String name, String email, String password, String phoneNo) {
       this.Name = name;
        this.Email = email;
        this.Password = password;
        this.PhoneNo = phoneNo;
    }
    
    public void saveToDatabase() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        MongoCollection<Document> collection = database.getCollection("profiles");
        
        Document doc = new Document("Name", this.Name)
                .append("Email", this.Email)
                .append("Password", this.Password)
                .append("PhoneNo", this.PhoneNo);
        
        collection.insertOne(doc);
        System.out.println("Profile saved to MongoDB");
    }
}

public class profiles {
    public static void main(String[] args) {
        profile admin = new profile("Thamaraiselvan", "sarothamaraiselvan@gmail.com" , "admin@123","8148427563");
        profile user = new profile("Sarothamaraiselvan", "sarothamaraiselvan579@gmail.com", "user@123", "8667823231");
        System.out.println("Admin Profile:");
        System.out.println("Name: " + admin.Name);
        System.out.println("Email: " + admin.Email);
        System.out.println("Password: " + admin.Password);
        System.out.println("Phone No: " + admin.PhoneNo);
        System.out.println("\nUser Profile:");
        System.out.println("Name: " + user.Name);
        System.out.println("Email: " + user.Email);
        System.out.println("Password: " + user.Password);
        System.out.println("Phone No: " + user.PhoneNo);
        
        admin.saveToDatabase();
        user.saveToDatabase();
        MongoDBConnection.close();
    }
}
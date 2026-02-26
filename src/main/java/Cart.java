import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final List<CartItem> items;
    
    public Cart() {
        this.items = new ArrayList<>();
    }
    
    public void addItem(String productName, double price, int quantity) {
        // Check if item already exists in cart
        for (CartItem item : items) {
            if (item.getProductName().equals(productName)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        // Add new item
        items.add(new CartItem(productName, price, quantity));
    }
    
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }
    
    public void clearCart() {
        items.clear();
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    public double getTotalAmount() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getTotalPrice();
        }
        return total;
    }
    
    public int getTotalItems() {
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
}

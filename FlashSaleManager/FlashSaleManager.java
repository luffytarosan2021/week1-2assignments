import java.util.*;
import java.util.concurrent.*;

public class FlashSaleManager {
    // Thread-safe map for stock levels
    private final Map<String, Integer> stockMap = new ConcurrentHashMap<>();

    // Map to hold waiting lists for each product (FIFO order)
    private final Map<String, Queue<Integer>> waitingLists = new ConcurrentHashMap<>();

    // Initialize stock for a product
    public void addProduct(String productId, int initialStock) {
        stockMap.put(productId, initialStock);
        waitingLists.put(productId, new LinkedList<>());
    }

    public String purchaseItem(String productId, int userId) {
        // Synchronize on the unique string instance of the productId
        // to prevent race conditions on that specific item.
        synchronized (productId.intern()) {
            int currentStock = stockMap.getOrDefault(productId, 0);

            if (currentStock > 0) {
                stockMap.put(productId, currentStock - 1);
                return "Success! " + (currentStock - 1) + " units remaining for " + productId;
            } else {
                // Stock is out, add to waiting list
                Queue<Integer> queue = waitingLists.get(productId);
                queue.add(userId);
                return "Out of stock. Added userId " + userId + " to waiting list at position #" + queue.size();
            }
        }
    }

    public int checkStock(String productId) {
        return stockMap.getOrDefault(productId, 0);
    }

    public static void main(String[] args) {
        FlashSaleManager system = new FlashSaleManager();
        String item = "IPHONE15_256GB";

        system.addProduct(item, 100);

        // Simulated concurrent purchases
        System.out.println(system.checkStock(item) + " units available");
        System.out.println(system.purchaseItem(item, 12345));
        System.out.println(system.purchaseItem(item, 67890));

        // Simulate stock exhaustion (manually setting to 0 for demo)
        for(int i = 0; i < 98; i++) system.purchaseItem(item, i);

        // The 101st person
        System.out.println(system.purchaseItem(item, 99999));
    }
}
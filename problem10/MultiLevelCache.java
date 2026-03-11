import java.util.*;

class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true); // access-order
        this.capacity = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}

public class MultiLevelCache {

    // L1 (Memory)
    private LRUCache<String, String> L1 = new LRUCache<>(10000);

    // L2 (SSD simulated)
    private LRUCache<String, String> L2 = new LRUCache<>(100000);

    // L3 (Database simulated)
    private HashMap<String, String> database = new HashMap<>();

    // Access tracking
    private HashMap<String, Integer> accessCount = new HashMap<>();

    // Statistics
    int L1Hits = 0;
    int L2Hits = 0;
    int L3Hits = 0;

    public String getVideo(String videoId) {

        // Check L1
        if (L1.containsKey(videoId)) {
            L1Hits++;
            System.out.println("L1 Cache HIT");
            return L1.get(videoId);
        }

        // Check L2
        if (L2.containsKey(videoId)) {
            L2Hits++;
            System.out.println("L2 Cache HIT");

            promoteToL1(videoId);

            return L2.get(videoId);
        }

        // Check L3 Database
        if (database.containsKey(videoId)) {

            L3Hits++;
            System.out.println("L3 Database HIT");

            // Add to L2
            L2.put(videoId, database.get(videoId));

            accessCount.put(videoId, 1);

            return database.get(videoId);
        }

        return null;
    }

    private void promoteToL1(String videoId) {

        int count = accessCount.getOrDefault(videoId, 0) + 1;

        accessCount.put(videoId, count);

        if (count >= 2) {
            L1.put(videoId, L2.get(videoId));
        }
    }

    public void getStatistics() {

        int total = L1Hits + L2Hits + L3Hits;

        System.out.println("\nCache Statistics:");

        if (total == 0) {
            System.out.println("No requests yet.");
            return;
        }

        System.out.println("L1 Hit Rate: " + (L1Hits * 100.0 / total) + "%");
        System.out.println("L2 Hit Rate: " + (L2Hits * 100.0 / total) + "%");
        System.out.println("L3 Hit Rate: " + (L3Hits * 100.0 / total) + "%");
    }

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        // Simulated database videos
        cache.database.put("video_123", "Breaking News Video");
        cache.database.put("video_999", "Sports Highlights");

        System.out.println("Request 1:");
        cache.getVideo("video_123");

        System.out.println("\nRequest 2:");
        cache.getVideo("video_123");

        System.out.println("\nRequest 3:");
        cache.getVideo("video_999");

        cache.getStatistics();
    }
}
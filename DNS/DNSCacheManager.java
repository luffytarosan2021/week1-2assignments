import java.util.*;

public class DNSCacheManager {

    // Custom Entry class to store IP and Time metadata
    class DNSEntry {
        String ipAddress;
        long expiryTime; // Store as System.currentTimeMillis() + TTL

        DNSEntry(String ipAddress, int ttlSeconds) {
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000L);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final int CAPACITY;
    private final Map<String, DNSEntry> cache;
    private int hits = 0;
    private int misses = 0;

    public DNSCacheManager(int capacity) {
        this.CAPACITY = capacity;
        // LinkedHashMap with accessOrder=true handles the LRU logic automatically
        this.cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > CAPACITY;
            }
        };
    }

    public String resolve(String domain) {
        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                return "Cache HIT → " + entry.ipAddress;
            } else {
                // Entry exists but is old
                cache.remove(domain);
            }
        }

        // Cache MISS logic
        misses++;
        String ipFromUpstream = mockUpstreamQuery(domain);
        // Let's assume a default TTL of 5 seconds for testing
        cache.put(domain, new DNSEntry(ipFromUpstream, 5));
        return "Cache MISS/EXPIRED → Query upstream → " + ipFromUpstream;
    }

    private String mockUpstreamQuery(String domain) {
        // Simulating a real DNS lookup
        return "172.217." + (int)(Math.random() * 255) + "." + (int)(Math.random() * 255);
    }

    public void getCacheStats() {
        double total = hits + misses;
        double hitRate = (total == 0) ? 0 : (hits / total) * 100;
        System.out.println("--- Cache Stats ---");
        System.out.println("Hits: " + hits + " | Misses: " + misses);
        System.out.printf("Hit Rate: %.2f%%\n", hitRate);
    }

    public static void main(String[] args) throws InterruptedException {
        DNSCacheManager dns = new DNSCacheManager(3); // Small capacity to test LRU

        System.out.println(dns.resolve("google.com"));
        System.out.println(dns.resolve("google.com")); // Should be HIT

        System.out.println("\nWaiting for TTL to expire (6 seconds)...");
        Thread.sleep(6000);

        System.out.println(dns.resolve("google.com")); // Should be EXPIRED/MISS
        dns.getCacheStats();
    }
}
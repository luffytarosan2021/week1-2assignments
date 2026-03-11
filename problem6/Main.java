import java.util.*;

class TokenBucket {

    int tokens;
    int maxTokens;
    long lastRefillTime;
    int refillRate;

    TokenBucket(int maxTokens, int refillRate) {
        this.tokens = maxTokens;
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.lastRefillTime = System.currentTimeMillis();
    }

    void refill() {

        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;

        int tokensToAdd = (int)(elapsed / 3600000.0 * refillRate);

        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }

    boolean allowRequest() {

        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }
}

class RateLimiter {

    HashMap<String, TokenBucket> clients = new HashMap<>();

    public void checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId, new TokenBucket(1000, 1000));

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {
            System.out.println("Allowed (" + bucket.tokens + " remaining)");
        } else {
            System.out.println("Denied - Rate limit exceeded");
        }
    }
}

public class Main {
    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        limiter.checkRateLimit("abc123");
        limiter.checkRateLimit("abc123");
    }
}
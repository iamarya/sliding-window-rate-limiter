import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowRateLimiter implements RateLimiter {

    int noOfBuckets;
    int rate;
    Duration rateDuration;
    Duration slidingDuration;
    Lock lock;
    Map<Integer, Integer> timeSlots;
    Instant lastFetched;

    public SlidingWindowRateLimiter(int rate, Duration rateDuration, Duration slidingDuration) {
        if (rateDuration.toMillis() < slidingDuration.toMillis()) {
            throw new RuntimeException("slidingDuration should be more than rateDuration");
        }
        if (rateDuration.toMillis() % slidingDuration.toMillis() > 0) {
            throw new RuntimeException("rateDuration should be multiple of slidingDuration");
        }
        this.rate = rate;
        this.rateDuration = rateDuration;
        this.slidingDuration = slidingDuration;
        this.noOfBuckets = (int) (rateDuration.toMillis() / slidingDuration.toMillis());
        this.timeSlots = new LinkedHashMap<>(noOfBuckets);
        this.lastFetched = Instant.now();
        for (int i = 0; i < noOfBuckets; i++) {
            timeSlots.put(i, 0);
        }
        lock = new ReentrantLock();
    }

    public SlidingWindowRateLimiter(int rate, Duration rateDuration) {
        this(rate, rateDuration, Duration.ofSeconds(1));
    }

    public void acquire() throws InterruptedException {
        if (lock.tryLock(rateDuration.toSeconds() + 1, TimeUnit.SECONDS)) {
            try {
                while (true) {
                    Instant now = Instant.now();
                    int key = (int) (now.toEpochMilli() % noOfBuckets);
                    int lastKey = (int) (lastFetched.toEpochMilli() % noOfBuckets);

                    if (!Duration.between(lastFetched, now).minus(rateDuration).isNegative()) {
                        // last fetched is more than the duration so make all counts 0
                        for (int i = 0; i < noOfBuckets; i++) {
                            timeSlots.put(i, 0);
                        }
                    } else if (key == lastKey) {
                        // do nothing
                    } else if (key == lastKey + 1) {
                        // start of new bucket
                        timeSlots.put(key, 0);
                    } else {
                        // move from lastKey+1 to currentKey-1 in a circular fashion
                        for (int i = lastKey + 1; i % noOfBuckets != key; i++) {
                            timeSlots.put(i, 0);
                        }
                        timeSlots.put(key, 0);
                    }
                    // calculate total count
                    int total = timeSlots.values().stream().mapToInt(i -> i).sum();
                    if (total < rate) {
                        timeSlots.put(key, timeSlots.get(key) + 1);
                        lastFetched = now;
                        break;
                    } else {
                        Thread.sleep(1000);
                    }
                }
            } finally {
                lock.unlock();
            }
        } else {
            throw new RuntimeException("Time out exception");
        }
    }


    public static void main(String[] args) throws InterruptedException {
        SlidingWindowRateLimiter r = new SlidingWindowRateLimiter(4, Duration.ofSeconds(4),
                Duration.ofSeconds(2));
        for (int i = 0; i < 100; i++) {
            r.acquire();
            System.out.println("LoopStart " + i);
            //Thread.sleep(2000);
            //System.out.println("LoopEnd "+i);
        }
    }
}

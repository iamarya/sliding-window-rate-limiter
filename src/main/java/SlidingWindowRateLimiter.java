import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class SlidingWindowRateLimiter implements RateLimiter{

    int rate;
    Duration rateDuration;
    Duration windowSlideDuration;


    public SlidingWindowRateLimiter(int rate, Duration rateDuration, Duration windowSlideDuration) {
        this.rate=rate;
        this.rateDuration=rateDuration;
        this.windowSlideDuration=windowSlideDuration;
    }

    public SlidingWindowRateLimiter(int rate, Duration rateDuration) {
        this.rate=rate;
        this.rateDuration=rateDuration;
        this.windowSlideDuration=Duration.ofSeconds(1);
    }


    public static void main(String[] args) {
        SlidingWindowRateLimiter r = new SlidingWindowRateLimiter(10, Duration.ofMinutes(1), Duration.ofSeconds(2));

    }
}

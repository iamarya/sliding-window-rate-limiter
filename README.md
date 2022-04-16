# Sliding Window Rate limiter
\#sliding-window-rate-limiter

This is generic library project and can be integrated with any java project. The main reason behind making this project is to implement a sliding window rate limiter. If you have used google guava library you must aware about the popular rate limiter it has and it is true that in may places it serves the purpose also. But when I come up a situation when I need a rate limiter with a sliding window control, I did not found a suitable solution any where. So started developing this project.

## What is sliding window rate limiter?
Okay, before that what is rate limiter. In real life many api, DB call or method calls are throttled, means you cant call a particular service more that the configured value. If you call more that the threshold value, you will get a response with message "too many calls". To handle this situation, so that your calls never cross this threshold and wait if has crossed till it can safely able to execute. This is what rate liter is and a simple example will be 10 calls are allowed per user in a minute. Problem with this type of rate limiter is what will happen if 10 calls happened at the end of a minute and at start of the next minute another 10 calls executed. So what will happen in this case. If the rate limiter don't uses a sliding window it will basically pass the execution. This is the problem with guava rate limiter. 
The way I manage to handle the mention challenge by using a window of time which has a configurable sliding speed and the total count will be calculated on every call on this slide window. 

## How to use this project?

Use the constructor `public SlidingWindowRateLimiter(int rate, Duration rateDuration, Duration slidingDuration)` to create the object of SlidingWindowRateLimiter. Where rate is the rate of calls per rateDuration. Whereas slidingDuration is the duration after which the duration window will slide. Below is the example.

```java
SlidingWindowRateLimiter rateLimiter = new SlidingWindowRateLimiter(4, Duration.ofSeconds(4),
                Duration.ofSeconds(2));
// rate limiter will allow 4 calls per 4 sec and the window will slide after every 2 secs. 
```

```java
// Other constructor for defalu sliding speed of 1 sec
public SlidingWindowRateLimiter(int rate, Duration rateDuration)

SlidingWindowRateLimiter rateLimiter = new SlidingWindowRateLimiter(4, Duration.ofSeconds(4));
```
I have used duration as it will allow versatile rate like 10calls/6Hrs or 60 calls/5min.

### Conditions or checks
There are two checks I have applied.
1. rateDuration >= slidingDuration
2. rateDuration % slidingDuration == 0; i.e. rate duration should be a whole multiple of slidingDuration. For example if rateDuration is 1 minute slidingDuration can't be 13 secs, it can be 1 sec, 2 sec, 3 sec, 5 sec etc.


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

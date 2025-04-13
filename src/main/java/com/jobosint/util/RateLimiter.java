package com.jobosint.util;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe rate limiter that can be used across multiple threads.
 * Uses a token bucket algorithm to limit the rate of operations.
 */
@Slf4j
public class RateLimiter {
    private final String name;
    private final int maxRequestsPerPeriod;
    private final long periodInMillis;
    private final Semaphore semaphore;
    private final AtomicInteger availableTokens;
    private volatile long lastRefillTimestamp;
    
    // Static map to hold rate limiters by name for singleton access
    private static final ConcurrentHashMap<String, RateLimiter> LIMITERS = new ConcurrentHashMap<>();
    
    // Delay queue for LinkedIn requests
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
    
    // Queue for LinkedIn requests to ensure they're processed sequentially with proper delays
    private static final BlockingQueue<Runnable> LINKEDIN_QUEUE = new LinkedBlockingQueue<>();
    
    // Worker thread that processes LinkedIn requests from the queue
    private static final Thread LINKEDIN_WORKER;
    
    // Flag to control the worker thread
    private static volatile boolean running = true;
    
    static {
        // Initialize the LinkedIn worker thread
        LINKEDIN_WORKER = new Thread(() -> {
            log.info("Starting LinkedIn request queue worker thread");
            while (running) {
                try {
                    Runnable task = LINKEDIN_QUEUE.poll(1, TimeUnit.SECONDS);
                    if (task != null) {
                        task.run();
                        // Add a fixed delay between LinkedIn requests to prevent rate limiting
                        Thread.sleep(3000); // 3 second delay between requests
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("LinkedIn worker thread interrupted", e);
                } catch (Exception e) {
                    log.error("Error in LinkedIn worker thread", e);
                }
            }
            log.info("LinkedIn request queue worker thread shutting down");
        });
        LINKEDIN_WORKER.setDaemon(true);
        LINKEDIN_WORKER.setName("LinkedIn-Request-Worker");
        LINKEDIN_WORKER.start();
        
        // Add shutdown hook to clean up resources
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            SCHEDULER.shutdownNow();
            LINKEDIN_WORKER.interrupt();
            log.info("Rate limiter resources cleaned up");
        }));
    }

    /**
     * Creates a new rate limiter with the specified parameters.
     *
     * @param name The name of this rate limiter
     * @param maxRequestsPerPeriod Maximum number of requests allowed in the specified period
     * @param period The time period
     * @param unit The time unit of the period
     */
    private RateLimiter(String name, int maxRequestsPerPeriod, long period, TimeUnit unit) {
        this.name = name;
        this.maxRequestsPerPeriod = maxRequestsPerPeriod;
        this.periodInMillis = unit.toMillis(period);
        this.semaphore = new Semaphore(maxRequestsPerPeriod, true);
        this.availableTokens = new AtomicInteger(maxRequestsPerPeriod);
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    /**
     * Gets or creates a rate limiter with the specified parameters.
     *
     * @param name The name of the rate limiter
     * @param maxRequestsPerPeriod Maximum number of requests allowed in the specified period
     * @param period The time period
     * @param unit The time unit of the period
     * @return A rate limiter instance
     */
    public static RateLimiter getInstance(String name, int maxRequestsPerPeriod, long period, TimeUnit unit) {
        return LIMITERS.computeIfAbsent(name, k -> new RateLimiter(name, maxRequestsPerPeriod, period, unit));
    }

    /**
     * Gets a LinkedIn specific rate limiter with default settings.
     * Limits to 10 requests per minute.
     *
     * @return A rate limiter for LinkedIn
     */
    public static RateLimiter getLinkedInInstance() {
        return getInstance("LinkedIn", 10, 1, TimeUnit.MINUTES);
    }

    /**
     * Acquires a permit from this rate limiter, blocking until one is available.
     *
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public void acquire() throws InterruptedException {
        refillTokens();
        log.debug("[{}] Waiting for rate limit permit ({} available)", name, availableTokens.get());
        semaphore.acquire();
        availableTokens.decrementAndGet();
        log.debug("[{}] Acquired rate limit permit ({} remaining)", name, availableTokens.get());
    }

    /**
     * Acquires a permit from this rate limiter if one is available within the specified waiting time.
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return true if a permit was acquired and false if the waiting time elapsed before a permit was acquired
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        refillTokens();
        log.debug("[{}] Trying to acquire rate limit permit with timeout ({} available)", name, availableTokens.get());
        boolean acquired = semaphore.tryAcquire(timeout, unit);
        if (acquired) {
            availableTokens.decrementAndGet();
            log.debug("[{}] Acquired rate limit permit ({} remaining)", name, availableTokens.get());
        } else {
            log.debug("[{}] Failed to acquire rate limit permit within timeout", name);
        }
        return acquired;
    }

    /**
     * Releases a permit, returning it to the rate limiter.
     */
    public void release() {
        semaphore.release();
        availableTokens.incrementAndGet();
        log.debug("[{}] Released rate limit permit ({} available)", name, availableTokens.get());
    }

    /**
     * Refills tokens based on elapsed time since last refill.
     */
    private synchronized void refillTokens() {
        long now = System.currentTimeMillis();
        long timeSinceLastRefill = now - lastRefillTimestamp;
        
        if (timeSinceLastRefill < periodInMillis) {
            return; // Not time to refill yet
        }
        
        // Calculate how many periods have passed and how many tokens to add
        long periodsElapsed = timeSinceLastRefill / periodInMillis;
        if (periodsElapsed > 0) {
            int tokensToAdd = (int) Math.min(maxRequestsPerPeriod, periodsElapsed * maxRequestsPerPeriod);
            int currentTokens = availableTokens.get();
            int newTokens = Math.min(maxRequestsPerPeriod, currentTokens + tokensToAdd);
            
            // Release additional permits to the semaphore
            int permitsToRelease = newTokens - currentTokens;
            if (permitsToRelease > 0) {
                semaphore.release(permitsToRelease);
                availableTokens.set(newTokens);
                log.debug("[{}] Refilled {} tokens ({} now available)", name, permitsToRelease, newTokens);
            }
            
            lastRefillTimestamp = now;
        }
    }

    /**
     * Execute an action with rate limiting.
     *
     * @param action The action to execute
     * @param <T> The return type of the action
     * @return The result of the action
     * @throws Exception if the action throws an exception
     */
    public <T> T executeWithRateLimit(RateLimitedAction<T> action) throws Exception {
        try {
            acquire();
            return action.execute();
        } finally {
            release();
        }
    }
    
    /**
     * Queue a LinkedIn request to be executed with proper rate limiting.
     * This method will add the request to a queue that is processed by a single worker thread
     * with fixed delays between requests.
     *
     * @param action The action to execute
     * @param <T> The return type of the action
     * @return A CompletableFuture that will complete with the result of the action
     */
    public static <T> CompletableFuture<T> queueLinkedInRequest(RateLimitedSupplier<T> action) {
        CompletableFuture<T> future = new CompletableFuture<>();
        
        LINKEDIN_QUEUE.offer(() -> {
            try {
                T result = action.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }
    
    /**
     * Schedule a LinkedIn request to be executed after a delay.
     * This is useful for spreading out requests over time.
     *
     * @param action The action to execute
     * @param delay The delay before executing the action
     * @param unit The time unit of the delay
     * @param <T> The return type of the action
     * @return A CompletableFuture that will complete with the result of the action
     */
    public static <T> CompletableFuture<T> scheduleLinkedInRequest(RateLimitedSupplier<T> action, long delay, TimeUnit unit) {
        CompletableFuture<T> future = new CompletableFuture<>();
        
        SCHEDULER.schedule(() -> {
            try {
                T result = action.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, delay, unit);
        
        return future;
    }

    /**
     * Functional interface for actions that can be rate limited.
     *
     * @param <T> The return type of the action
     */
    @FunctionalInterface
    public interface RateLimitedAction<T> {
        T execute() throws Exception;
    }
    
    /**
     * Functional interface for suppliers that can be rate limited.
     *
     * @param <T> The return type of the supplier
     */
    @FunctionalInterface
    public interface RateLimitedSupplier<T> {
        T get() throws Exception;
    }
}

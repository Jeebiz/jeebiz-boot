package net.jeebiz.boot.extras.redis.setup.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "spring.redis.execution")
public class RedisExecutionProperties {

    /**
     * listener pool configuration.
     */
    private final Pool listener = new Pool();

    /**
     * subscription pool configuration.
     */
    private final Pool subscription = new Pool();

    public Pool getListener() {
        return listener;
    }

    public Pool getSubscription() {
        return subscription;
    }

    /**
     * Pool properties.
     */
    public static class Pool {

        /**
         * Set the ThreadPoolExecutor's core pool size. Default is 1.
         * positive.
         */
        private int coreSize = 1;

        /**
         * Set the ThreadPoolExecutor's maximum pool size. Default is the number of Processor.
         */
        private int maxSize = Runtime.getRuntime().availableProcessors();

        /**
         * Set the capacity for the ThreadPoolExecutor's BlockingQueue. Default is Integer.MAX_VALUE.
         * Any positive value will lead to a LinkedBlockingQueue instance; any other value will lead to a SynchronousQueue instance.
         */
        private int queueCapacity = Integer.MAX_VALUE;

        /**
         * Set the ThreadPoolExecutor's keep-alive time. Default is 60 seconds.
         */
        private Duration keepAlive = Duration.ofSeconds(60);

        /**
         * Specify whether to allow core threads to time out. This enables dynamic
         * growing and shrinking even in combination with a non-zero queue (since
         * the max pool size will only grow once the queue is full).
         * <p>Default is "false".
         */
        private boolean allowCoreThreadTimeOut = false;

        private String threadNamePrefix = "redis-execution-";


        public void setCoreSize(int coreSize) {
            this.coreSize = coreSize;
        }

        public int getCoreSize() {
            return coreSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public int getQueueCapacity() {
            return queueCapacity;
        }

        public void setKeepAlive(Duration keepAlive) {
            this.keepAlive = keepAlive;
        }

        public Duration getKeepAlive() {
            return keepAlive;
        }

        public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
            this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        }

        public boolean isAllowCoreThreadTimeOut() {
            return allowCoreThreadTimeOut;
        }

        public void setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        public String getThreadNamePrefix() {
            return threadNamePrefix;
        }
    }
}

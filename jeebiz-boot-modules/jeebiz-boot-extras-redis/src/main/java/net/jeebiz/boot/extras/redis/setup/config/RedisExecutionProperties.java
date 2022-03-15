package net.jeebiz.boot.extras.redis.setup.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "spring.redis.execution")
public class RedisExecutionProperties {

    private final Execution listener = new Execution();

    private final Execution subscription = new Execution();

    public Execution getListener() {
        return listener;
    }

    public Execution getSubscription() {
        return subscription;
    }

    /**
     * Execution properties.
     */
    public static class Execution {

        /**
         * Executor pool configuration.
         */
        private Pool pool;

        public Pool getPool() {
            return this.pool;
        }

        public void setPool(Pool pool) {
            this.pool = pool;
        }

    }

    /**
     * Pool properties.
     */
    public static class Pool {

        /**
         * Set the ThreadPoolExecutor's maximum pool size. Default is the number of Processor.
         */
        private int maxIdle = Runtime.getRuntime().availableProcessors();

        /**
         * Set the ThreadPoolExecutor's core pool size. Default is 1.
         * positive.
         */
        private int minIdle = 1;

        /**
         * Set the capacity for the ThreadPoolExecutor's BlockingQueue. Default is Integer.MAX_VALUE.
         * Any positive value will lead to a LinkedBlockingQueue instance; any other value will lead to a SynchronousQueue instance.
         */
        private int maxActive = Integer.MAX_VALUE;

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


        public int getMaxIdle() {
            return this.maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        public int getMinIdle() {
            return this.minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public int getMaxActive() {
            return this.maxActive;
        }

        public void setMaxActive(int maxActive) {
            this.maxActive = maxActive;
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


    }
}

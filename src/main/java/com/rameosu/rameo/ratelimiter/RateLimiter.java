package com.rameosu.rameo.ratelimiter;

/**
 * @description: 限流工具类，仅适用于单机架构
 * @author: suwei
 * @since: 2020/10/19 12:35 PM
 */
public class RateLimiter {
    /**
     * 限流工具初始化系统时间
     */
    private static long currentTime = System.currentTimeMillis();

    /**
     * 固定时间访问次数阈值
     */
    private static final int LIMIT = 10;

    /**
     * 计数器更新间隔时间
     */
    private static final long INTERVAL = 1000;

    /**
     * 计数器当前访问次数
     */
    private static long reqCount = 0;

    /**
     * 漏桶当前水量
     */
    private static long water = 0;

    /**
     * 漏桶流出速率
     */
    private static long leakyRate = 1;

    /**
     * 漏桶容量
     */
    private static long leakyCapacity = 100;

    /**
     * 令牌生成速率
     */
    private static long tokenRate = 1;

    /**
     * 令牌桶容量
     */
    private static long tokenCapacity = 100;

    /**
     * 令牌桶当前令牌数
     */
    private static long token = 0;


    /**
     * 限流-计数器算法(固定窗口)
     * 时间段内允许访问的数量是固定的
     */
    public static boolean counterAlgorithm() {
        long now = System.currentTimeMillis();
        if (now < currentTime + INTERVAL) {
            reqCount++;
            return reqCount <= LIMIT;
        } else {
            currentTime = now;
            reqCount = 1;
        }
        return false;
    }

    /**
     * 限流-漏桶算法
     * 以固定速度流出，以任意速度流入，达到最大值则触发限流
     */
    public static boolean leakyBucketAlgorithm() {
        long now = System.currentTimeMillis();
        water = Math.max(0, water - (now - currentTime) * leakyRate);
        currentTime = now;
        if (water <= leakyCapacity) {
            water++;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 限流-令牌桶算法
     * 以固定速度往桶内放入令牌，以任意速度拿出令牌，令牌为空则触发限流
     */
    public static boolean tokenBucketAlgorithm() {
        long now = System.currentTimeMillis();
        token = Math.min(tokenCapacity, token + (now - currentTime) * tokenRate);
        currentTime = now;
        if (token < 1) {
            return false;
        } else {
            token--;
            return true;
        }
    }
}



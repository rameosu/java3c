package com.rameosu.rameo.ratelimiter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @description: 限流工具类，仅适用于单机架构
 * @author: suwei
 * @since: 2020/10/19 12:35 PM
 */
public class RateLimiter {

    /**
     * 漏桶当前水量
     */
    private long water = 0;

    /**
     * 漏桶流出速率
     */
    private long leakyRate = 1;

    /**
     * 漏桶容量
     */
    private long leakyCapacity = 100;

    /**
     * 令牌生成速率
     */
    private long tokenRate = 1;

    /**
     * 令牌桶容量
     */
    private long tokenCapacity = 100;

    /**
     * 令牌桶当前令牌数
     */
    private long token = 0;

    /**
     * 限流工具初始化系统时间
     */
    private long currentTime = System.currentTimeMillis();

    /**
     * 固定时间访问次数阈值
     */
    private int LIMIT = 10;

    /**
     * 计数器更新间隔时间
     */
    private long INTERVAL = 1000;

    /**
     * 计数器当前访问次数
     */
    private long reqCount = 0;

    /**
     * 限流-计数器算法(固定窗口)
     * 时间段内允许访问的数量是固定的
     */
    public boolean counterAlgorithm() {
        long now = System.currentTimeMillis();
        if (now < currentTime + INTERVAL) { // 在窗口期内
            reqCount++; // 计数器当前窗口期访问次数+1
            return reqCount <= LIMIT; // 小于窗口期内的访问次数限制，返回true，请求放行
        } else { // 不在窗口期内
            currentTime = now; // 重置窗口期开始时间
            reqCount = 0; // 访问次数清零
        }
        return false; // 请求拦截
    }

    /**
     * 单位时间划分的小周期（单位时间是1分钟，10s一个小格子窗口，一共6个格子）
     */
    private int SUB_CYCLE = 10;

    /**
     * 每分钟限流请求数
     */
    private int thresholdPerMin = 100;

    /**
     * 计数器, k-为当前窗口的开始时间值秒，value为当前窗口的计数
     */
    private final Map<Long, Integer> counters = new HashMap<>();

    /**
     * 滑动窗口时间算法实现
     */
    public boolean slidingWindowsTryAcquire() {
        long currentWindowTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) / SUB_CYCLE; // 获取当前时间在哪个小周期窗口
        int currentWindowNum = countCurrentWindow(currentWindowTime); // 当前窗口总请求数
        // 超过阀值限流
        if (currentWindowNum >= thresholdPerMin) {
            return false;
        }
        // 计数器+1
        Integer windowTime = counters.get(currentWindowTime);
        windowTime++;
        counters.put(currentWindowTime, windowTime);
        return true;
    }

    /**
     * 统计当前窗口的请求数
     */
    private int countCurrentWindow(long currentWindowTime) {
        // 计算窗口开始位置
        long startTime = currentWindowTime - SUB_CYCLE * (60 / SUB_CYCLE - 1);
        int count = 0;

        // 遍历存储的计数器
        Iterator<Map.Entry<Long, Integer>> iterator = counters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Integer> entry = iterator.next();
            // 删除无效过期的子窗口计数器
            if (entry.getKey() < startTime) {
                iterator.remove();
            } else {
                // 累加当前窗口的所有计数器之和
                count = count + entry.getValue();
            }
        }
        return count;
    }


    /**
     * 限流-漏桶算法
     * 以固定速度流出，以任意速度流入，达到最大值则触发限流
     */
    public boolean leakyBucketAlgorithm() {
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
    public boolean tokenBucketAlgorithm() {
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



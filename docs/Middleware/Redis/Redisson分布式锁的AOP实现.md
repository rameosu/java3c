

# Redisson分布式锁的AOP实现

### 理论部分

在之前的文章[Redis分布式锁](http://localhost:3000/#/Redis/Redis分布式锁?id=redis分布式锁)中，介绍了通过redis实现分布锁的两种方式，分别是：

1. 通过redis自带的命令：setNX
2. 通过redis的客户端：redisson

作者更加推荐使用redisson客户端的方式，因为redisson支持更多的锁类型，譬如**联锁、红锁、读写锁、公平锁等**，而且redisson的实现更加简单，开发者只需要调用响应的`API`即可，无需关心底层加锁的过程和解锁的`原子性`问题。

在[Redis分布式锁](http://localhost:3000/#/Redis/Redis分布式锁?id=redis分布式锁)中，列出了redisson对于多种的锁类型的简单实现，即编程式实现。这样的实现完全能够满足我们的日常开发需求，但是缺点也很明显。

譬如：

- **代码嵌入较多，不够优雅**
- **重复代码**
- **对锁的参数运用不直观**
- **容易忘掉解锁的步骤**

> 使用过Spring的同学，肯定都知道@Transactional注解，Spring即支持编程式事务，也支持注解式（声明式）事务。

我们是否也可以参考这样的实现呢？

答案是：完全OK！

`AOP`就是专门干这种事的。

### 实战部分

### 1、引入redisson依赖

```xml
<dependency>
	<groupId>org.redisson</groupId>
	<artifactId>redisson</artifactId>
	<version>3.16.2</version>
</dependency>
```

### 2、自定义注解

```java
/**
 * 分布式锁自定义注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {

    /**
     * 锁的模式：如果不设置自动模式，当参数只有一个.使用 REENTRANT 参数多个 MULTIPLE
     */
    LockModel lockModel() default LockModel.AUTO;

    /**
     * 如果keys有多个，如果不设置，则使用 联锁
     *
     * @return
     */
    String[] keys() default {};

    /**
     * key的静态常量：当key的spel的值是LIST、数组时使用+号连接将会被spel认为这个变量是个字符串，只能产生一把锁，达不到我们的目的，
     * 而我们如果又需要一个常量的话。这个参数将会在拼接在每个元素的后面
     *
     * @return
     */
    String keyConstant() default "";

    /**
     * 锁超时时间,默认30000毫秒(可在配置文件全局设置)
     *
     * @return
     */
    long watchDogTimeout() default 30000;

    /**
     * 等待加锁超时时间，默认10000毫秒 -1 则表示一直等待(可在配置文件全局设置)
     *
     * @return
     */
    long attemptTimeout() default 10000;
}
```

### 3、常量类

```java
/**
 * Redisson常量类
 */
public class RedissonConst {
    /**
     * redisson锁默认前缀
     */
    public static final String REDISSON_LOCK = "redisson:lock:";
    /**
     * spel表达式占位符
     */
    public static final String PLACE_HOLDER = "#";
}
```

### 4、枚举

```java
/**
 * 锁的模式
 */
public enum LockModel {
    /**
     * 可重入锁
     */
    REENTRANT,
    /**
     * 公平锁
     */
    FAIR,
    /**
     * 联锁
     */
    MULTIPLE,
    /**
     * 红锁
     */
    RED_LOCK,
    /**
     * 读锁
     */
    READ,
    /**
     * 写锁
     */
    WRITE,
    /**
     * 自动模式，当参数只有一个使用 REENTRANT 参数多个 RED_LOCK
     */
    AUTO
}
```

### 5、自定义异常

```java
/**
 * 分布式锁异常
 */
public class ReddissonException extends RuntimeException {

    public ReddissonException() {
    }

    public ReddissonException(String message) {
        super(message);
    }

    public ReddissonException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReddissonException(Throwable cause) {
        super(cause);
    }

    public ReddissonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
```

### 6、AOP切面

```java
/**
 * 分布式锁aop
 */
@Slf4j
@Aspect
public class LockAop {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedissonProperties redissonProperties;

    @Autowired
    private LockStrategyFactory lockStrategyFactory;

    @Around("@annotation(lock)")
    public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint, Lock lock) throws Throwable {
        // 需要加锁的key数组
        String[] keys = lock.keys();
        if (ArrayUtil.isEmpty(keys)) {
            throw new ReddissonException("redisson lock keys不能为空");
        }
        // 获取方法的参数名
        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(((MethodSignature) proceedingJoinPoint.getSignature()).getMethod());
        Object[] args = proceedingJoinPoint.getArgs();
        // 等待锁的超时时间
        long attemptTimeout = lock.attemptTimeout();
        if (attemptTimeout == 0) {
            attemptTimeout = redissonProperties.getAttemptTimeout();
        }
        // 锁超时时间
        long lockWatchdogTimeout = lock.watchdogTimeout();
        if (lockWatchdogTimeout == 0) {
            lockWatchdogTimeout = redissonProperties.getLockWatchdogTimeout();
        }
        // 加锁模式
        LockModel lockModel = getLockModel(lock, keys);
        if (!lockModel.equals(LockModel.MULTIPLE) && !lockModel.equals(LockModel.RED_LOCK) && keys.length > 1) {
            throw new ReddissonException("参数有多个，锁模式为->" + lockModel.name() + "，无法匹配加锁");
        }
        log.info("锁模式->{}，等待锁定时间->{}毫秒，锁定最长时间->{}毫秒", lockModel.name(), attemptTimeout, lockWatchdogTimeout);
        boolean res = false;
        // 策略模式获取redisson锁对象
        RLock rLock = lockStrategyFactory.createLock(lockModel, keys, parameterNames, args, lock.keyConstant(), redissonClient);
        //执行aop
        if (rLock != null) {
            try {
                if (attemptTimeout == -1) {
                    res = true;
                    //一直等待加锁
                    rLock.lock(lockWatchdogTimeout, TimeUnit.MILLISECONDS);
                } else {
                    res = rLock.tryLock(attemptTimeout, lockWatchdogTimeout, TimeUnit.MILLISECONDS);
                }
                if (res) {
                    return proceedingJoinPoint.proceed();
                } else {
                    throw new ReddissonException("获取锁失败");
                }
            } finally {
                if (res) {
                    rLock.unlock();
                }
            }
        }
        throw new ReddissonException("获取锁失败");
    }

    /**
     * 获取加锁模式
     *
     * @param lock
     * @param keys
     * @return
     */
    private LockModel getLockModel(Lock lock, String[] keys) {
        LockModel lockModel = lock.lockModel();
        // 自动模式：优先匹配全局配置，再判断用红锁还是可重入锁
        if (lockModel.equals(LockModel.AUTO)) {
            LockModel globalLockModel = redissonProperties.getLockModel();
            if (globalLockModel != null) {
                lockModel = globalLockModel;
            } else if (keys.length > 1) {
                lockModel = LockModel.RED_LOCK;
            } else {
                lockModel = LockModel.REENTRANT;
            }
        }
        return lockModel;
    }
}
```

这里使用了`策略模式`来对不同的锁类型提供实现。

### 7、锁策略的实现

先定义锁策略的**抽象基类**（也可以用**接口**）：

```java
/**
 * 锁策略抽象基类
 */
@Slf4j
abstract class LockStrategy {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 创建RLock
     *
     * @param keys
     * @param parameterNames
     * @param args
     * @param keyConstant
     * @return
     */
    abstract RLock createLock(String[] keys, String[] parameterNames, Object[] args, String keyConstant, RedissonClient redissonClient);

    /**
     * 获取RLock
     *
     * @param keys
     * @param parameterNames
     * @param args
     * @param keyConstant
     * @return
     */
    public RLock[] getRLocks(String[] keys, String[] parameterNames, Object[] args, String keyConstant) {
        List<RLock> rLocks = new ArrayList<>();
        for (String key : keys) {
            List<String> valueBySpel = getValueBySpel(key, parameterNames, args, keyConstant);
            for (String s : valueBySpel) {
                rLocks.add(redissonClient.getLock(s));
            }
        }
        RLock[] locks = new RLock[rLocks.size()];
        int index = 0;
        for (RLock r : rLocks) {
            locks[index++] = r;
        }
        return locks;
    }

    /**
     * 通过spring Spel 获取参数
     *
     * @param key            定义的key值 以#开头 例如:#user
     * @param parameterNames 形参
     * @param args           形参值
     * @param keyConstant    key的常亮
     * @return
     */
    List<String> getValueBySpel(String key, String[] parameterNames, Object[] args, String keyConstant) {
        List<String> keys = new ArrayList<>();
        if (!key.contains(PLACE_HOLDER)) {
            String s = REDISSON_LOCK + key + keyConstant;
            log.info("没有使用spel表达式value->{}", s);
            keys.add(s);
            return keys;
        }
        // spel解析器
        ExpressionParser parser = new SpelExpressionParser();
        // spel上下文
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        Expression expression = parser.parseExpression(key);
        Object value = expression.getValue(context);
        if (value != null) {
            if (value instanceof List) {
                List valueList = (List) value;
                for (Object o : valueList) {
                    keys.add(REDISSON_LOCK + o.toString() + keyConstant);
                }
            } else if (value.getClass().isArray()) {
                Object[] objects = (Object[]) value;
                for (Object o : objects) {
                    keys.add(REDISSON_LOCK + o.toString() + keyConstant);
                }
            } else {
                keys.add(REDISSON_LOCK + value.toString() + keyConstant);
            }
        }
        log.info("spel表达式key={},value={}", key, keys);
        return keys;
    }
}
```

再提供各种锁模式的具体实现：

- **可重入锁：**

```java
/**
 * 可重入锁策略
 */
public class ReentrantLockStrategy extends LockStrategy {

    @Override
    public RLock createLock(String[] keys, String[] parameterNames, Object[] args, String keyConstant, RedissonClient redissonClient) {
        List<String> valueBySpel = getValueBySpel(keys[0], parameterNames, args, keyConstant);
        //如果spel表达式是数组或者集合 则使用红锁
        if (valueBySpel.size() == 1) {
            return redissonClient.getLock(valueBySpel.get(0));
        } else {
            RLock[] locks = new RLock[valueBySpel.size()];
            int index = 0;
            for (String s : valueBySpel) {
                locks[index++] = redissonClient.getLock(s);
            }
            return new RedissonRedLock(locks);
        }
    }
}
```

- **公平锁：**

```java
/**
 * 公平锁策略
 */
public class FairLockStrategy extends LockStrategy {

    @Override
    public RLock createLock(String[] keys, String[] parameterNames, Object[] args, String keyConstant, RedissonClient redissonClient) {
        return redissonClient.getFairLock(getValueBySpel(keys[0], parameterNames, args, keyConstant).get(0));
    }
}
```

- **联锁**

```java
/**
 * 联锁策略
 */
public class MultipleLockStrategy extends LockStrategy {

    @Override
    public RLock createLock(String[] keys, String[] parameterNames, Object[] args, String keyConstant, RedissonClient redissonClient) {
        RLock[] locks = getRLocks(keys, parameterNames, args, keyConstant);
        return new RedissonMultiLock(locks);
    }
}
```

- **红锁**

```java
/**
 * 红锁策略
 */
public class RedLockStrategy extends LockStrategy {

    @Override
    public RLock createLock(String[] keys, String[] parameterNames, Object[] args, String keyConstant, RedissonClient redissonClient) {
        RLock[] locks = getRLocks(keys, parameterNames, args, keyConstant);
        return new RedissonRedLock(locks);
    }
}
```

- **读锁**

```java
/**
 * 读锁策略
 */
public class ReadLockStrategy extends LockStrategy {

    @Override
    public RLock createLock(String[] keys, String[] parameterNames, Object[] args, String keyConstant, RedissonClient redissonClient) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(getValueBySpel(keys[0], parameterNames, args, keyConstant).get(0));
        return rwLock.readLock();
    }
}
```

- **写锁**

```java
/**
 * 写锁策略
 */
public class WriteLockStrategy extends LockStrategy {

    @Override
    public RLock createLock(String[] keys, String[] parameterNames, Object[] args, String keyConstant, RedissonClient redissonClient) {
        RReadWriteLock rwLock = redissonClient.getReadWriteLock(getValueBySpel(keys[0], parameterNames, args, keyConstant).get(0));
        return rwLock.writeLock();
    }
}
```

最后提供一个**策略工厂**初始化锁策略：

```java
/**
 * 锁的策略工厂
 */
@Service
public class LockStrategyFactory {

    private LockStrategyFactory() {
    }

    private static final Map<LockModel, LockStrategy> STRATEGIES = new HashMap<>(6);

    static {
        STRATEGIES.put(LockModel.FAIR, new FairLockStrategy());
        STRATEGIES.put(LockModel.REENTRANT, new ReentrantLockStrategy());
        STRATEGIES.put(LockModel.RED_LOCK, new RedLockStrategy());
        STRATEGIES.put(LockModel.READ, new ReadLockStrategy());
        STRATEGIES.put(LockModel.WRITE, new WriteLockStrategy());
        STRATEGIES.put(LockModel.MULTIPLE, new MultipleLockStrategy());
    }

    public RLock createLock(LockModel lockModel, String[] keys, String[] parameterNames, Object[] args, String keyConstant, RedissonClient redissonClient) {
        return STRATEGIES.get(lockModel).createLock(keys, parameterNames, args, keyConstant, redissonClient);
    }
}
```

大功告成，顺利吃鸡:hatching_chick::hatched_chick::baby_chick:
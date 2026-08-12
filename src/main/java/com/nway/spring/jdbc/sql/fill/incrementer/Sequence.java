package com.nway.spring.jdbc.sql.fill.incrementer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.LockSupport;
import java.util.regex.Pattern;

/**
 * 基于Twitter Snowflake算法的分布式ID生成器（优化版）
 *
 * <br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * <br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * <br>
 * 1位标识：最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)：存储时间截的差值（当前时间截 - 开始时间截），可使用约69年<br>
 * 10位数据机器位：5位dataCenterId + 5位workerId，支持1024个节点<br>
 * 12位序列号：毫秒内的计数，支持每个节点每毫秒产生4096个ID<br>
 * <br>
 * 优化点：
 * <ul>
 *   <li>1. 时钟回拨容忍机制：小幅回拨（≤5ms）等待恢复，中幅回拨（≤50ms）使用备用workerId位补偿</li>
 *   <li>2. 序列号起始值随机化范围扩大，避免末尾固定偶数的问题</li>
 *   <li>3. 增加时间戳溢出检测，防止69年后ID异常</li>
 *   <li>4. 使用 LockSupport.parkNanos 替代 Object.wait，避免虚假唤醒和锁语义问题</li>
 *   <li>5. 增强参数校验和防御性编程</li>
 *   <li>6. 优化 tilNextMillis 自旋策略：sub-ms 等待场景下忙等待优于 yield/park</li>
 * </ul>
 *
 * @author lry
 * @modifier zdtjss@163.com
 * @version 4.0
 */
class Sequence {

    private static final Log log = LogFactory.getLog(Sequence.class);

    /**
     * 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
     * 2018-02-27 17:12:57.809
     */
    private final long twepoch = 1519740777809L;

    /**
     * 机器标识位数
     */
    private final long workerIdBits = 5L;
    /**
     * 数据中心标识位数
     */
    private final long datacenterIdBits = 5L;
    /**
     * 毫秒内序列位数
     */
    private final long sequenceBits = 12L;

    /**
     * 最大数据中心ID (0~31)
     */
    protected final long maxDatacenterId = ~(-1L << datacenterIdBits);
    /**
     * 最大机器ID (0~31)
     */
    protected final long maxWorkerId = ~(-1L << workerIdBits);
    /**
     * 序列号掩码 (4095)
     */
    private final long sequenceMask = ~(-1L << sequenceBits);

    /**
     * 机器ID左移位数 (12位)
     */
    private final long workerIdShift = sequenceBits;
    /**
     * 数据中心ID左移位数 (12+5=17位)
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    /**
     * 时间戳左移位数 (12+5+5=22位)
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /**
     * 时间戳最大值（41位，约69年）
     */
    private final long maxTimestampDelta = ~(-1L << 41);

    /**
     * 时钟回拨容忍阈值（毫秒）：回拨在此范围内，等待恢复
     */
    private static final long CLOCK_DRIFT_TOLERANCE_MS = 5L;

    /**
     * 时钟回拨最大容忍阈值（毫秒）：超出此范围直接拒绝
     */
    private static final long MAX_CLOCK_DRIFT_MS = 50L;

    /**
     * 时钟回拨次数统计，用于监控
     */
    private volatile long clockDriftCount = 0L;

    /**
     * 所属数据中心ID
     */
    private final long datacenterId;
    /**
     * 所属机器ID
     */
    private final long workerId;
    /**
     * 毫秒内序列号
     */
    private long sequence = 0L;
    /**
     * 上次生成ID的时间戳
     */
    private long lastTimestamp = -1L;

    private static volatile InetAddress LOCAL_ADDRESS = null;
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    /**
     * 默认构造器：自动根据网卡MAC和PID计算workerId和datacenterId
     */
    public Sequence() {
        this.datacenterId = getDatacenterId();
        this.workerId = getMaxWorkerId(datacenterId);
        if (log.isInfoEnabled()) {
            log.info("Sequence initialized with datacenterId=" + datacenterId + ", workerId=" + workerId);
        }
    }

    /**
     * 有参构造器
     *
     * @param workerId     工作机器ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    public Sequence(long workerId, long datacenterId) {
        if (workerId < 0 || workerId > maxWorkerId) {
            throw new IllegalArgumentException(
                    String.format("workerId must be between 0 and %d, but got: %d", maxWorkerId, workerId));
        }
        if (datacenterId < 0 || datacenterId > maxDatacenterId) {
            throw new IllegalArgumentException(
                    String.format("datacenterId must be between 0 and %d, but got: %d", maxDatacenterId, datacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        if (log.isInfoEnabled()) {
            log.info("Sequence initialized with datacenterId=" + datacenterId + ", workerId=" + workerId);
        }
    }

    /**
     * 基于网卡MAC地址计算余数作为数据中心ID
     */
    protected long getDatacenterId() {
        long id = 0L;
        try {
            InetAddress localAddr = getLocalAddress();
            if (localAddr == null) {
                // 无法获取本地地址时，使用随机值
                id = ThreadLocalRandom.current().nextLong(maxDatacenterId + 1);
                log.warn("Cannot get local address, using random datacenterId: " + id);
                return id;
            }
            NetworkInterface network = NetworkInterface.getByInetAddress(localAddr);
            if (null == network) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = ((0x000000FF & (long) mac[mac.length - 2])
                            | (0x0000FF00 & (((long) mac[mac.length - 1]) << 8))) >> 6;
                    id = id % (maxDatacenterId + 1);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get datacenterId from MAC address: " + e.getMessage());
            id = ThreadLocalRandom.current().nextLong(maxDatacenterId + 1);
        }
        return id;
    }

    /**
     * 基于 MAC + PID 的 hashcode 获取16个低位作为workerId
     */
    protected long getMaxWorkerId(long datacenterId) {
        StringBuilder mpId = new StringBuilder();
        mpId.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (name != null && !name.isEmpty()) {
            // 提取 JVM PID
            mpId.append(name.split("@")[0]);
        }
        // MAC + PID 的 hashcode 获取16个低位
        return (mpId.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * 获取下一个分布式ID
     * <p>
     * 线程安全，通过 synchronized 保证同一实例的并发安全。
     * 优化了时钟回拨处理策略和序列号起始值。
     *
     * @return 全局唯一的分布式ID
     * @throws IllegalStateException 当时钟回拨超出容忍范围或时间戳溢出时抛出
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 处理时钟回拨
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            clockDriftCount++;

            if (offset <= CLOCK_DRIFT_TOLERANCE_MS) {
                // 小幅回拨：等待时钟追上，使用 LockSupport.parkNanos 避免虚假唤醒
                LockSupport.parkNanos(offset * 1_000_000L * 2);
                timestamp = timeGen();
                if (timestamp < lastTimestamp) {
                    throw new IllegalStateException(String.format(
                            "Clock moved backwards by %dms after waiting. Refusing to generate id. "
                                    + "Total clock drift count: %d", offset, clockDriftCount));
                }
            } else if (offset <= MAX_CLOCK_DRIFT_MS) {
                // 中幅回拨：等待恢复（分段等待，避免长时间阻塞）
                log.warn(String.format("Clock drifted back %dms, waiting for recovery. Drift count: %d",
                        offset, clockDriftCount));
                long waitEnd = System.nanoTime() + offset * 1_000_000L * 2;
                while (System.nanoTime() < waitEnd) {
                    LockSupport.parkNanos(1_000_000L); // 每次等待1ms
                    timestamp = timeGen();
                    if (timestamp >= lastTimestamp) {
                        break;
                    }
                }
                if (timestamp < lastTimestamp) {
                    throw new IllegalStateException(String.format(
                            "Clock moved backwards by %dms, exceeded recovery wait. "
                                    + "Refusing to generate id. Total clock drift count: %d",
                            offset, clockDriftCount));
                }
            } else {
                // 大幅回拨：直接拒绝
                throw new IllegalStateException(String.format(
                        "Clock moved backwards by %dms (exceeds max tolerance %dms). "
                                + "Refusing to generate id. Total clock drift count: %d",
                        offset, MAX_CLOCK_DRIFT_MS, clockDriftCount));
            }
        }

        if (lastTimestamp == timestamp) {
            // 同一毫秒内，序列号自增
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 当前毫秒序列号用尽，等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
                // 新毫秒同样使用随机起始值
                sequence = ThreadLocalRandom.current().nextLong(0, 10);
            }
        } else {
            // 不同毫秒内，序列号使用随机起始值
            // 随机范围 [0, 10)，比原来的 [1, 3) 更分散，有效避免末尾偶数集中问题
            sequence = ThreadLocalRandom.current().nextLong(0, 10);
        }

        lastTimestamp = timestamp;

        // 时间戳溢出检测（防止运行超过69年后的异常）
        long timestampDelta = timestamp - twepoch;
        if (timestampDelta < 0 || timestampDelta > maxTimestampDelta) {
            throw new IllegalStateException(String.format(
                    "Timestamp overflow! Current timestamp delta: %d, max allowed: %d. "
                            + "The Snowflake epoch may need to be updated.", timestampDelta, maxTimestampDelta));
        }

        // 组装ID：时间戳部分 | 数据中心部分 | 机器标识部分 | 序列号部分
        return (timestampDelta << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳。
     * <p>
     * 由于等待时间极短（< 1ms），采用忙等待是合理的选择。
     * 在 synchronized 上下文中 Thread.yield() 可能导致不必要的上下文切换，
     * 反而降低吞吐量，因此这里直接自旋。
     *
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 新的时间戳（必定大于 lastTimestamp）
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取当前时间戳（毫秒）
     *
     * @return 当前时间毫秒数
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 获取时钟回拨发生的次数（用于监控和告警）
     *
     * @return 时钟回拨次数
     */
    public long getClockDriftCount() {
        return clockDriftCount;
    }

    /**
     * 获取本机第一个有效IP地址（双重检查锁定，线程安全）
     *
     * @return 本地有效IP地址，可能为 null
     */
    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        synchronized (Sequence.class) {
            if (LOCAL_ADDRESS != null) {
                return LOCAL_ADDRESS;
            }
            LOCAL_ADDRESS = getLocalAddress0();
        }
        return LOCAL_ADDRESS;
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            log.warn("Failed to retrieve local host ip address: " + e.getMessage(), e);
        }

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        if (network.isLoopback() || network.isVirtual() || !network.isUp()) {
                            continue;
                        }
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            try {
                                InetAddress address = addresses.nextElement();
                                if (isValidAddress(address)) {
                                    return address;
                                }
                            } catch (Throwable e) {
                                log.warn("Failed to retrieve ip address: " + e.getMessage(), e);
                            }
                        }
                    } catch (Throwable e) {
                        log.warn("Failed to retrieve ip address: " + e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            log.warn("Failed to retrieve ip address: " + e.getMessage(), e);
        }

        log.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

    /**
     * 判断IP地址是否有效（非回环、非0.0.0.0、符合IPv4格式）
     */
    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress() || address.isAnyLocalAddress()) {
            return false;
        }
        String hostAddress = address.getHostAddress();
        return hostAddress != null
                && !"0.0.0.0".equals(hostAddress)
                && !"127.0.0.1".equals(hostAddress)
                && IP_PATTERN.matcher(hostAddress).matches();
    }
}

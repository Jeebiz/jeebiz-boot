package io.hiwepy.boot.api.sequence;


import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

/**
 *
 * 引用 hutool中基于Twitter的Snowflake算法实现分布式高效有序ID
 */
@Slf4j
public class Sequence {

    private final Snowflake snowflake;
    private static byte LAST_IP = 0;

    /**
     * 获取单例的Twitter的Snowflake 算法生成器对象<br>
     * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
     *
     * <p>
     * snowflake的结构如下(每部分用-分开):<br>
     *
     * <pre>
     * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
     * </pre>
     * <p>
     * 第一位为未使用，接下来的41位为毫秒级时间(41位的长度可以使用69年)<br>
     * 然后是5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点）<br>
     * 最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
     *
     * <p>
     * 参考：http://www.cnblogs.com/relucent/p/4955340.html
     *
     * @param workerId            工作机器节点id,数据范围为0~31
     * @return {@link Sequence}
     * @since 1.0.0
     */
    public Sequence(long workerId) {
        this.snowflake = new Snowflake( workerId);
    }

    /**
     * 获取单例的Twitter的Snowflake 算法生成器对象<br>
     * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
     *
     * <p>
     * snowflake的结构如下(每部分用-分开):<br>
     *
     * <pre>
     * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
     * </pre>
     * <p>
     * 第一位为未使用，接下来的41位为毫秒级时间(41位的长度可以使用69年)<br>
     * 然后是5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点）<br>
     * 最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
     *
     * <p>
     * 参考：http://www.cnblogs.com/relucent/p/4955340.html
     *
     * @param workerId            工作机器节点id,数据范围为0~31
     * @param dataCenterId        数据中心id,数据范围为0~31
     * @return {@link Sequence}
     * @since 1.0.0
     */
    public Sequence(long workerId, long dataCenterId) {
        this.snowflake = new Snowflake( workerId, dataCenterId);
    }

    /**
     * 获取单例的Twitter的Snowflake 算法生成器对象<br>
     * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
     *
     * <p>
     * snowflake的结构如下(每部分用-分开):<br>
     *
     * <pre>
     * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
     * </pre>
     * <p>
     * 第一位为未使用，接下来的41位为毫秒级时间(41位的长度可以使用69年)<br>
     * 然后是5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点）<br>
     * 最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
     *
     * <p>
     * 参考：http://www.cnblogs.com/relucent/p/4955340.html
     *
     * @param workerId            工作机器节点id,数据范围为0~31
     * @param dataCenterId        数据中心id,数据范围为0~31
     * @param isUseSystemClock    是否使用{@link cn.hutool.core.date.SystemClock} 获取当前时间戳
     * @return {@link Sequence}
     * @since 1.0.0
     */
    public Sequence(long workerId, long dataCenterId, boolean isUseSystemClock) {
        this.snowflake = new Snowflake( workerId, dataCenterId, isUseSystemClock);
    }

    /**
     * 获取单例的Twitter的Snowflake 算法生成器对象<br>
     * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
     *
     * <p>
     * snowflake的结构如下(每部分用-分开):<br>
     *
     * <pre>
     * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
     * </pre>
     * <p>
     * 第一位为未使用，接下来的41位为毫秒级时间(41位的长度可以使用69年)<br>
     * 然后是5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点）<br>
     * 最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
     *
     * <p>
     * 参考：http://www.cnblogs.com/relucent/p/4955340.html
     *
     * @param workerId            工作机器节点id,数据范围为0~31
     * @param dataCenterId        数据中心id,数据范围为0~31
     * @param isUseSystemClock    是否使用{@link cn.hutool.core.date.SystemClock} 获取当前时间戳
     * @param timeOffset          允许时间回拨的毫秒数
     * @return {@link Sequence}
     * @since 1.0.0
     */
    public Sequence(long workerId, long dataCenterId, boolean isUseSystemClock, long timeOffset) {
        this.snowflake = new Snowflake( null, workerId, dataCenterId, isUseSystemClock, timeOffset);
    }

    /**
     * 获取单例的Twitter的Snowflake 算法生成器对象<br>
     * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
     *
     * <p>
     * snowflake的结构如下(每部分用-分开):<br>
     *
     * <pre>
     * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
     * </pre>
     * <p>
     * 第一位为未使用，接下来的41位为毫秒级时间(41位的长度可以使用69年)<br>
     * 然后是5位datacenterId和5位workerId(10位的长度最多支持部署1024个节点）<br>
     * 最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
     *
     * <p>
     * 参考：http://www.cnblogs.com/relucent/p/4955340.html
     *
     * @param workerId            工作机器节点id,数据范围为0~31
     * @param dataCenterId        数据中心id,数据范围为0~31
     * @param isUseSystemClock    是否使用{@link cn.hutool.core.date.SystemClock} 获取当前时间戳
     * @param timeOffset          允许时间回拨的毫秒数
     * @param randomSequenceLimit 限定一个随机上限，在不同毫秒下生成序号时，给定一个随机数，避免偶数问题，0表示无随机，上限不包括值本身。
     * @return {@link Sequence}
     * @since 1.0.0
     */
    public Sequence(long workerId, long dataCenterId, boolean isUseSystemClock, long timeOffset, long randomSequenceLimit) {
        this.snowflake = new Snowflake( null, workerId, dataCenterId, isUseSystemClock, timeOffset, randomSequenceLimit);
    }

    /**
     * 获取ID
     *
     * @return long
     */
    public synchronized Long nextId() {
        // 使用snowflake获取ID
        long nextId = this.snowflake.nextId();
        // 直接返回ID
        return nextId;
    }

    public Snowflake getSnowflake() {
        return snowflake;
    }

    /**
     * 用IP地址最后几个字节标示
     * <p>
     * eg:192.168.1.30->30
     *
     * @return last IP
     */
    public static byte getLastIPAddress() {
        if (LAST_IP != 0) {
            return LAST_IP;
        }

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            byte[] addressByte = inetAddress.getAddress();
            LAST_IP = addressByte[addressByte.length - 1];
            log.info("Host Address : {}", inetAddress.getHostAddress());
        } catch (Exception e) {
            throw new RuntimeException("Unknown Host Exception", e);
        }

        return LAST_IP;
    }


}
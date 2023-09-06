package io.hiwepy.boot.autoconfigure.sequence;

import io.hiwepy.boot.api.sequence.Sequence;
import org.springframework.data.redis.core.RedisOperationTemplate;

public class GlobalSequence extends Sequence {

    private RedisOperationTemplate redisOperation;

    public GlobalSequence(RedisOperationTemplate redisOperation, long workerId) {
        super(workerId);
        this.redisOperation = redisOperation;
    }

    public GlobalSequence(RedisOperationTemplate redisOperation, long workerId, long dataCenterId) {
        super(workerId, dataCenterId);
        this.redisOperation = redisOperation;
    }

    public GlobalSequence(RedisOperationTemplate redisOperation, long workerId, long dataCenterId, boolean isUseSystemClock) {
        super(workerId, dataCenterId, isUseSystemClock);
        this.redisOperation = redisOperation;
    }

    public GlobalSequence(RedisOperationTemplate redisOperation, long workerId, long dataCenterId, boolean isUseSystemClock, long timeOffset) {
        super(workerId, dataCenterId, isUseSystemClock, timeOffset);
        this.redisOperation = redisOperation;
    }

    public GlobalSequence(RedisOperationTemplate redisOperation, long workerId, long dataCenterId, boolean isUseSystemClock, long timeOffset, long randomSequenceLimit) {
        super(workerId, dataCenterId, isUseSystemClock, timeOffset, randomSequenceLimit);
        this.redisOperation = redisOperation;
    }

    @Override
    public synchronized Long nextId() {
        // 使用snowflake获取ID
        long nextId = this.getSnowflake().nextId();
        // 使用分布式布隆过滤器判断ID是否重复
        //redisOperation.getBit(nextId)

        return nextId;

    }


}

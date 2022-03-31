package net.jeebiz.boot.extras.redis.setup;

import org.springframework.data.redis.core.RedisOperationTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BizRedisTemplate {

    private RedisOperationTemplate redisOperation;

    public BizRedisTemplate(RedisOperationTemplate redisOperation) {
        this.redisOperation = Objects.requireNonNull(redisOperation);
    }

    // ===============================batchGet=================================

    public <K> List<Map<String, Object>> batchGetUserInfo(Collection<K> uids) {
        Collection<Object> uKeys = uids.stream().map(uid -> {
            return BizRedisKey.USER_INFO.getKey(Objects.toString(uid));
        }).collect(Collectors.toList());
        return redisOperation.hmMultiGetAll(uKeys);
    }

    public <K> Map<String, Object> batchGetUserFields(K uid, Collection<Object> hashKeys) {
        String userKey = BizRedisKey.USER_INFO.getKey(Objects.toString(uid));
        return redisOperation.hmMultiGet(userKey, hashKeys);
    }

    public <K> Map<String, Object> batchGetUserFields(K uid, String... hashKeys) {
        String userKey = BizRedisKey.USER_INFO.getKey(Objects.toString(uid));
        return redisOperation.hmMultiGet(userKey, Stream.of(hashKeys).collect(Collectors.toList()));
    }

    public <K> List<Map<String, Object>> batchGetUserFields(Collection<K> uids, String... hashKeys) {
        List<String> uKeys = uids.stream().map(uid -> {
            return BizRedisKey.USER_INFO.getKey(Objects.toString(uid));
        }).collect(Collectors.toList());
        return redisOperation.hmMultiGet(uKeys, Stream.of(hashKeys).collect(Collectors.toList()));
    }

    public <K> List<Map<String, Object>> batchGetUserFields(Collection<K> uids, Collection<Object> hashKeys) {
        List<String> uKeys = uids.stream().map(uid -> {
            return BizRedisKey.USER_INFO.getKey(Objects.toString(uid));
        }).collect(Collectors.toList());
        return redisOperation.hmMultiGet(uKeys, hashKeys);
    }

    public <K> Map<String, Map<String, Object>> batchGetUserFields(Collection<K> uids, String identityField,
                                                                   Collection<Object> hashKeys) {
        List<String> uKeys = uids.stream().map(uid -> {
            return BizRedisKey.USER_INFO.getKey(Objects.toString(uid));
        }).collect(Collectors.toList());
        return redisOperation.hmMultiGet(uKeys, identityField, hashKeys);
    }

}

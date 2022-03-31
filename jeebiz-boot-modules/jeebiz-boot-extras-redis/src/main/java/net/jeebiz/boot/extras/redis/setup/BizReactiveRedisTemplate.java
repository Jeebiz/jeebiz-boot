package net.jeebiz.boot.extras.redis.setup;

import org.springframework.data.redis.core.ReactiveRedisOperationTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BizReactiveRedisTemplate {

    private ReactiveRedisOperationTemplate reactiveRedisOperation;

    public BizReactiveRedisTemplate(ReactiveRedisOperationTemplate reactiveRedisOperation) {
        this.reactiveRedisOperation = Objects.requireNonNull(reactiveRedisOperation);
    }

    public <K> Flux<Map.Entry<Object, Object>> batchGetUserInfo(Collection<K> uids) {
        List<String> uKeys = uids.stream().map(uid -> {
            return BizRedisKey.USER_INFO.getKey(Objects.toString(uid));
        }).collect(Collectors.toList());
        return reactiveRedisOperation.hmMultiGetAll(uKeys);
    }

    public <K> Mono<Map<String, Object>> batchGetUserFields(K uid, Collection<Object> hashKeys) {
        String userKey = BizRedisKey.USER_INFO.getKey(Objects.toString(uid));
        return reactiveRedisOperation.hmMultiGet(userKey, hashKeys);
    }

    public <K> Mono<Map<String, Object>> batchGetUserFields(K uid, String... hashKeys) {
        String userKey = BizRedisKey.USER_INFO.getKey(Objects.toString(uid));
        return reactiveRedisOperation.hmMultiGet(userKey, Stream.of(hashKeys).collect(Collectors.toList()));
    }

    public <K> Flux<Map<String, Object>> batchGetUserFields(Collection<K> uids, String... hashKeys) {
        List<String> uKeys = uids.stream().map(uid -> {
            return BizRedisKey.USER_INFO.getKey(Objects.toString(uid));
        }).collect(Collectors.toList());
        return reactiveRedisOperation.hmMultiGet(uKeys, Stream.of(hashKeys).collect(Collectors.toList()));
    }

    public <K> Flux<Map<String, Object>> batchGetUserFields(Collection<K> uids, Collection<Object> hashKeys) {
        List<String> uKeys = uids.stream().map(uid -> {
            return BizRedisKey.USER_INFO.getKey(Objects.toString(uid));
        }).collect(Collectors.toList());
        return reactiveRedisOperation.hmMultiGet(uKeys, hashKeys);
    }

    public <K> Mono<Map<String, Map<String, Object>>> batchGetUserFields(Collection<K> uids, String identityField,
                                                                         Collection<Object> hashKeys) {
        List<String> uKeys = uids.stream().map(uid -> {
            return BizRedisKey.USER_INFO.getKey(Objects.toString(uid));
        }).collect(Collectors.toList());
        return reactiveRedisOperation.hmMultiGet(uKeys, identityField, hashKeys);
    }

}

package net.jeebiz.boot.extras.redis.aspect;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import net.jeebiz.boot.api.ApiCode;
import net.jeebiz.boot.api.annotation.ApiIdempotent;
import net.jeebiz.boot.api.annotation.ApiIdempotentType;
import net.jeebiz.boot.api.exception.IdempotentException;
import net.jeebiz.boot.api.sequence.Sequence;
import net.jeebiz.boot.api.subject.AuthPrincipal;
import net.jeebiz.boot.api.subject.SubjectUtils;
import net.jeebiz.boot.api.utils.IdempotentUtils;
import net.jeebiz.boot.api.utils.WebUtils;
import net.jeebiz.boot.extras.redis.setup.RedisKey;
import net.jeebiz.boot.extras.redis.setup.RedisOperationTemplate;

/**
 * 1、基于Lua脚本实现分布式锁的方法
 * 2、参考：
 * https://blog.csdn.net/qq_24598601/article/details/105876432
 */
@Slf4j
public class ApiIdempotentLuaAspect {

	@Autowired
	private RedisOperationTemplate redisOperationTemplate;
	@Autowired
	private Sequence sequence;

	@Pointcut("@annotation(com.kding.boot.api.annotation.ApiIdempotent)")
	public void aspect() {
		// do nothing
	}

	@Around("aspect()")
	public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {

		// 1、获取方法
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		// 2、获取幂等注解
		ApiIdempotent idempotent = AnnotationUtils.findAnnotation(method, ApiIdempotent.class);
		if (Objects.isNull(idempotent)) {
			idempotent = AnnotationUtils.findAnnotation(method.getDeclaringClass(), ApiIdempotent.class);
		}
		// 3、不进行幂等
		if (Objects.isNull(idempotent)) {
			return joinPoint.proceed();
		}
		// 4、通过参数值构造唯一标记实现幂等
		if (ApiIdempotentType.ARGS.equals(idempotent.type())) {
			// 4.1、解析幂等唯一key
			String idempotentKey = IdempotentUtils.getIdempotentKey(joinPoint, idempotent);
			// 4.2、根据 key前缀 + @ApiIdempotent.value() + 方法签名 + 参数 构建缓存键值；确保幂等处理的操作对象是：同样的 @ApiIdempotent.value() + 方法签名 + 参数
			String uid = SubjectUtils.isAuthenticated() ? SubjectUtils.getPrincipal(AuthPrincipal.class).getUid() : "guest";
			String lockKey = RedisKey.IDEMPOTENT_ARGS_KEY.getKey(new StringJoiner("_").add(uid).add(idempotentKey).toString());
			String lockValue = sequence.nextId().toString();
			try {
				// 4.3、通过setnx确保只有一个接口能够正常访问
				if (redisOperationTemplate.tryLock(lockKey, lockValue, idempotent.expireMillis(), idempotent.retryTimes(), idempotent.retryInterval())) {
					return joinPoint.proceed();
				} else {
					log.debug("Idempotent hits, key=" + lockKey);
					throw new IdempotentException(ApiCode.SC_FAIL, "request.method.idempotent.hits");
				}
			} finally {
				if(idempotent.unlock()) {
					redisOperationTemplate.unlock(lockKey, lockValue);
				}
			}
		}

		// 5、通过请求参数中的token值实现幂等
		else if (ApiIdempotentType.TOKEN.equals(idempotent.type())) {
			// 5.1、获取Request对象
			HttpServletRequest request = WebUtils.getHttpServletRequest();
			// 5.2、从请求中获取token值
			String token = request.getHeader(idempotent.value());
			if (!StringUtils.hasText(token)) {
				token = request.getParameter(idempotent.value());
			}
			// 5.3、根据 key前缀 + token
			String lockKey = RedisKey.IDEMPOTENT_TOKEN_KEY.getKey(token);
			String lockValue = sequence.nextId().toString();
			try {
				// 5.4、通过setnx确保只有一个接口能够正常访问
				if (redisOperationTemplate.tryLock(lockKey, lockValue, idempotent.expireMillis(), idempotent.retryTimes(), idempotent.retryInterval())) {
					return joinPoint.proceed();
				} else {
					log.debug("Idempotent hits, key=" + lockKey);
					throw new IdempotentException(ApiCode.SC_FAIL, "request.method.idempotent.hits");
				}
			} finally {
				if(idempotent.unlock()) {
					redisOperationTemplate.unlock(lockKey, lockValue);
				}
			}
		}
		return joinPoint.proceed();
	}

}

package io.hiwepy.boot.autoconfigure.aspect;

import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.JoinPoint;
import org.springframework.util.StopWatch;

public interface ApiOperationLogProvider {


    default void doBefore(JoinPoint joinPoint, ApiOperation apiOperation) {

    }

    default void afterReturing(JoinPoint joinPoint, ApiOperation apiOperation, Object rt, StopWatch stopWatch) {

    }

    default Object wrapThrowing(JoinPoint joinPoint, ApiOperation apiOperation, Throwable ex, StopWatch stopWatch) throws Throwable {
        throw ex;
    }

    default void afterThrowing(JoinPoint joinPoint, ApiOperation apiOperation, Throwable ex, StopWatch stopWatch) {

    }

}

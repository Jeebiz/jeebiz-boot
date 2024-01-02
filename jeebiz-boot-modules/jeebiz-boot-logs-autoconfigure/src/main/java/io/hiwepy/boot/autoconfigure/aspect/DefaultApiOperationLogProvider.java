package io.hiwepy.boot.autoconfigure.aspect;

import io.hiwepy.boot.api.Constants;
import io.hiwepy.boot.api.utils.WebUtils;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StopWatch;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DefaultApiOperationLogProvider implements ApiOperationLogProvider {

    @Override
    public void doBefore(JoinPoint joinPoint, ApiOperation apiOperation) {

    }

    @Override
    public void afterReturing(JoinPoint joinPoint, ApiOperation apiOperation, Object rt, StopWatch stopWatch) {
        this.doApiOperationLog(joinPoint, apiOperation, rt, null, stopWatch);
    }

    @Override
    public void afterThrowing(JoinPoint joinPoint, ApiOperation apiOperation, Throwable ex, StopWatch stopWatch) {
        this.doApiOperationLog(joinPoint, apiOperation,null, ex, stopWatch);
    }

    protected void doApiOperationLog(JoinPoint joinPoint, ApiOperation apiOperation, Object rt, Throwable ex, StopWatch stopWatch) {

        // 1、获取AOP信息
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;

        // 2、获取方法及参数信息
        Method method = methodSignature.getMethod();
        String methodName = methodSignature.getName();

        // 3、获取 ApiIgnore 注解，如果获取到了，则不进行日志记录
        ApiIgnore apiIgnore = AnnotationUtils.findAnnotation(method, ApiIgnore.class);
        if (Objects.isNull(apiIgnore)) {
            apiIgnore = AnnotationUtils.findAnnotation(method.getDeclaringClass(), ApiIgnore.class);
        }

        // 4、判断是否需要记录日志
        boolean needLog = log.isInfoEnabled() && Objects.isNull(apiIgnore);
        if (!needLog) {
            log.info(Constants.accessMarker, stopWatch.prettyPrint());
            return;
        }

        // 5、获取 Request对象，解析请求来源
        HttpServletRequest request = WebUtils.getHttpServletRequest();
        String uri = "";
        String ipAddress = "";
        if (Objects.nonNull(request)) {
            uri = request.getRequestURI();
            ipAddress = WebUtils.getRemoteAddr(request);
            log.info(Constants.accessMarker, "Request ID {} >> URI {} IP {} Headers {} ", stopWatch.getId(), uri, ipAddress);
        }

        // 6、筛选出有意义的参数
        Object[] args = joinPoint.getArgs();
        List<Object> methodArgs = Objects.isNull(args) ? null : Stream.of(args)
                .filter(arg -> !(arg instanceof ServletRequest && arg instanceof ServletResponse))
                .collect(Collectors.toList());

        // 5、如果开启日志，则发送日志消息
        this.saveLog(joinPoint, method, apiOperation, rt, ex, stopWatch);

        if (Objects.isNull(ex)) {
            log.info(Constants.accessMarker, "Request ID {} >> invoke method {} with args {} Success!", stopWatch.getId(), methodName, methodArgs);
        } else {
            log.error(Constants.accessMarker, "Request ID {} >> invoke method {} with args {} error {} ", stopWatch.getId(), methodName, methodArgs, ex.getMessage());
        }

        log.info(Constants.accessMarker, stopWatch.prettyPrint());
    }

    protected void saveLog(JoinPoint joinPoint, Method method, ApiOperation apiOperation, Object rt, Throwable ex, StopWatch stopWatch){
        // do nothing
    }

}

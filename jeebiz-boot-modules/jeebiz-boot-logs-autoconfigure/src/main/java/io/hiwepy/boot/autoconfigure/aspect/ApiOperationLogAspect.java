package io.hiwepy.boot.autoconfigure.aspect;

import io.hiwepy.boot.api.XHeaders;
import io.hiwepy.boot.api.sequence.Sequence;
import io.hiwepy.boot.api.utils.WebUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class ApiOperationLogAspect {

    @Autowired
    private Sequence sequence;
    @Autowired
    private ApiOperationLogProvider logProvider;

    @Around("@annotation(io.swagger.annotations.ApiOperation) && @annotation(apiOperation)")
    public Object aroundMethod(ProceedingJoinPoint pjd, ApiOperation apiOperation) throws Throwable {

        // 1、创建并启动 StopWatch
        String requestId = this.getRequestId();
        StopWatch stopWatch = new StopWatch(requestId);
        stopWatch.start(Objects.nonNull(apiOperation.value()) ? apiOperation.value() : apiOperation.notes());

        try {

            // 2、开启日志记录
            logProvider.doBefore(pjd, apiOperation);

            // 3、执行代理方法
            Object result = null;
            try {
                result = pjd.proceed();
                return result;
            } finally {
                if (stopWatch.isRunning()) {
                    stopWatch.stop();
                }
                // 4、记录访问日志
                logProvider.afterReturing(pjd, apiOperation, result, stopWatch);
            }
        } catch (Throwable ex) {
            log.debug("Method invoke error !", ex);
            try {
                if (stopWatch.isRunning()) {
                    stopWatch.stop();
                }
                return logProvider.wrapThrowing(pjd, apiOperation, ex, stopWatch);
            } finally {
                // 5、记录异常日志
                logProvider.afterThrowing(pjd, apiOperation, ex, stopWatch);
            }
        } finally {
            MDC.clear();
        }
    }

    public static final String REQUEST_ID_KEY = "requestId";

    public String getRequestId() {
        HttpServletRequest request = WebUtils.getHttpServletRequest();
        String requestId = null;
        if (Objects.nonNull(request)) {
            String parameterRequestId = request.getParameter(REQUEST_ID_KEY);
            if (StringUtils.hasText(parameterRequestId)) {
                requestId = parameterRequestId;
            } else {
                String headerRequestId = request.getHeader(XHeaders.X_REQUEST_ID);
                if (StringUtils.hasText(headerRequestId)) {
                    requestId = headerRequestId;
                }
            }
        }
        if (!StringUtils.hasText(requestId)) {
            requestId = sequence.nextId().toString();
        }
        return requestId;
    }

}

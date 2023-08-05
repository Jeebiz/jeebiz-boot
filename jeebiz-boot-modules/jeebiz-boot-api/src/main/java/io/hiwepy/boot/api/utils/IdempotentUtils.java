package io.hiwepy.boot.api.utils;

import com.alibaba.fastjson.JSONObject;
import io.hiwepy.boot.api.annotation.ApiIdempotent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Stream;

@Slf4j
public class IdempotentUtils {

    protected static final ExpressionParser expressionParser = new SpelExpressionParser();

    public static String getIdempotentKey(ProceedingJoinPoint joinPoint, ApiIdempotent idempotent) throws IOException {
        // 1、获取方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] parameters = joinPoint.getArgs();
        // 4.1、在指定幂等值的情况下，判断是否需要进行 Spring Expression Language(SpEL) 表达式解析，如果需要，则进行SpEL解析
        if (StringUtils.hasText(idempotent.value())) {
            if (idempotent.spel()) {
                // 解析表达式需要的上下文，解析时有一个默认的上下文
                EvaluationContext context = new StandardEvaluationContext();
                for (int i = 0; i < parameterNames.length; i++) {
                    if (parameters[i] instanceof ServletRequest || parameters[i] instanceof ServletResponse) {
                        continue;
                    }
                    context.setVariable(parameterNames[i], parameters[i]);
                }
                return String.valueOf(expressionParser.parseExpression(idempotent.value()).getValue(context));
            }
            return idempotent.value();
        }
        // 4.2、没指定幂等值的情况下，尝试获取 @RequestMapping、@PostMapping、@GetMapping、@PutMapping、@DeleteMapping、@PatchMapping 的 value 值作为幂等值的一部分
        else {
            StringJoiner joiner = new StringJoiner("");
            // 4.2.1、优先获取Controller对象上的注解
            RequestMapping requestMapping = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RequestMapping.class);
            if (Objects.nonNull(requestMapping)) {
                log.debug("requestMapping: {}", JSONObject.toJSONString(requestMapping));
                Stream.of(Objects.isNull(requestMapping.value()) || ArrayUtils.isEmpty(requestMapping.value())
                        ? requestMapping.path()
                        : requestMapping.value()).findFirst().ifPresent(path -> {
                    joiner.add(path);
                });
            }
            // 4.2.2、获取方法上的PostMapping注解
            PostMapping postMapping = AnnotationUtils.findAnnotation(method, PostMapping.class);
            if (Objects.nonNull(postMapping)) {
                log.debug("postMapping: {}", JSONObject.toJSONString(postMapping));
                Stream.of(Objects.isNull(postMapping.value()) || ArrayUtils.isEmpty(postMapping.value())
                        ? postMapping.path()
                        : postMapping.value()).findFirst().ifPresent(path -> {
                    joiner.add(path);
                });
            }
            // 4.2.3、获取方法上的GetMapping注解
            GetMapping getMapping = AnnotationUtils.findAnnotation(method, GetMapping.class);
            if (Objects.nonNull(getMapping)) {
                Stream.of(Objects.isNull(getMapping.value()) || ArrayUtils.isEmpty(getMapping.value())
                        ? getMapping.path()
                        : getMapping.value()).findFirst().ifPresent(path -> {
                    joiner.add(path);
                });
                log.debug("getMapping: {}", JSONObject.toJSONString(getMapping));
                joiner.add(getMapping.path()[0]);
            }
            // 4.2.4、获取方法上的RequestMapping注解
            RequestMapping methodRequestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
            if (Objects.nonNull(methodRequestMapping)) {
                log.debug("requestMapping: {}", JSONObject.toJSONString(methodRequestMapping));
                Stream.of(Objects.isNull(methodRequestMapping.value()) || ArrayUtils.isEmpty(methodRequestMapping.value())
                        ? methodRequestMapping.path()
                        : methodRequestMapping.value()).findFirst().ifPresent(path -> {
                    joiner.add(path);
                });
            }
            // 4.2.5、获取方法上的DeleteMapping注解
            DeleteMapping deleteMapping = AnnotationUtils.findAnnotation(method, DeleteMapping.class);
            if (Objects.nonNull(deleteMapping)) {
                log.debug("deleteMapping: {}", JSONObject.toJSONString(deleteMapping));
                Stream.of(Objects.isNull(deleteMapping.value()) || ArrayUtils.isEmpty(deleteMapping.value())
                        ? deleteMapping.path()
                        : deleteMapping.value()).findFirst().ifPresent(path -> {
                    joiner.add(path);
                });
            }
            // 4.2.6、获取方法上的PatchMapping注解
            PatchMapping patchMapping = AnnotationUtils.findAnnotation(method, PatchMapping.class);
            if (Objects.nonNull(patchMapping)) {
                log.debug("patchMapping: {}", JSONObject.toJSONString(patchMapping));
                Stream.of(Objects.isNull(patchMapping.value()) || ArrayUtils.isEmpty(patchMapping.value())
                        ? patchMapping.path()
                        : patchMapping.value()).findFirst().ifPresent(path -> {
                    joiner.add(path);
                });
            }
            // 4.2.7、获取方法上的PutMapping注解
            PutMapping putMapping = AnnotationUtils.findAnnotation(method, PutMapping.class);
            if (Objects.nonNull(putMapping)) {
                log.debug("putMapping: {}", JSONObject.toJSONString(putMapping));
                Stream.of(Objects.isNull(putMapping.value()) || ArrayUtils.isEmpty(putMapping.value())
                        ? putMapping.path()
                        : putMapping.value()).findFirst().ifPresent(path -> {
                    joiner.add(path);
                });
            }
            // 根据{方法名 + 参数列表}
            if (idempotent.withArgs()) {
                Annotation[][] paramAnnotations = method.getParameterAnnotations();
                for (int i = 0; i < joinPoint.getArgs().length; i++) {
                    if (Stream.of(paramAnnotations[i]).anyMatch(annt -> annt instanceof ApiIgnore)) {
                        continue;
                    }
                    if (joinPoint.getArgs()[i] == null || joinPoint.getArgs()[i] instanceof ServletRequest || joinPoint.getArgs()[i] instanceof ServletResponse) {
                        continue;
                    }
                    joiner.add(JSONObject.toJSONString(joinPoint.getArgs()[i]));
                }
            }
            // 和md5转换生成key
            return DigestUtils.md5DigestAsHex(joiner.toString().getBytes());
        }
    }

}

package xyz.kbws.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.kbws.utils.TraceIdUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author kbws
 * @date 2024/11/25
 * @description: 请求响应日志 AOP
 */
@Slf4j
@Aspect
@Component
public class LogInterceptor {

    /**
     * 执行拦截
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        // 生成并设置 trace-id
        String traceId = TraceIdUtils.generateTraceId();
        TraceIdUtils.setTraceId(traceId);

        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 获取请求路径
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 生成请求唯一 id
        String requestId = UUID.randomUUID().toString();
        String url = httpServletRequest.getRequestURI();

        // 获取请求参数
        Object[] args = point.getArgs();
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";

        // 输出请求日志
        log.info("request start, id: {}, path: {}, ip: {}, params: {}", requestId, url,
                httpServletRequest.getRemoteHost(), reqParam);

        // 执行原方法
        Object result = point.proceed();


        // 输出响应日志
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
        // 清除 trace-id
        TraceIdUtils.remove();
        return result;
    }
}

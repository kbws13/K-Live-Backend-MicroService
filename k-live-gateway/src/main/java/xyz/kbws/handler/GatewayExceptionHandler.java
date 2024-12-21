package xyz.kbws.handler;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;
import xyz.kbws.common.BaseResponse;
import xyz.kbws.common.ErrorCode;
import xyz.kbws.exception.BusinessException;

import java.nio.charset.StandardCharsets;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 网关全局异常拦截器
 */
@Slf4j
@Component
public class GatewayExceptionHandler implements WebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        BaseResponse<String> res = getResponse(ex);
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSONUtil.toJsonStr(res).getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer));
    }

    private BaseResponse<String> getResponse(Throwable throwable) {
        BaseResponse<String> baseResponse;
        if (throwable instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) throwable;
            if (HttpStatus.NOT_FOUND == responseStatusException.getStatus()) {
                baseResponse = new BaseResponse<>(ErrorCode.NOT_FOUND_ERROR.getCode(), null, ErrorCode.NOT_FOUND_ERROR.getMessage());
                return baseResponse;
            } else if (HttpStatus.SERVICE_UNAVAILABLE == responseStatusException.getStatus()) {
                baseResponse = new BaseResponse<>(ErrorCode.SERVICE_UNAVAILABLE.getCode(), null, ErrorCode.SERVICE_UNAVAILABLE.getMessage());
                return baseResponse;
            } else {
                baseResponse = new BaseResponse<>(ErrorCode.SYSTEM_ERROR.getCode(), null, ErrorCode.SYSTEM_ERROR.getMessage());
                return baseResponse;
            }
        } else if (throwable instanceof BusinessException) {
            BusinessException businessException = (BusinessException) throwable;
            baseResponse = new BaseResponse<>(businessException.getCode(), businessException.getMessage());
            return baseResponse;
        }
        baseResponse = new BaseResponse<>(ErrorCode.SYSTEM_ERROR.getCode(), null, ErrorCode.SYSTEM_ERROR.getMessage());
        return baseResponse;
    }
}

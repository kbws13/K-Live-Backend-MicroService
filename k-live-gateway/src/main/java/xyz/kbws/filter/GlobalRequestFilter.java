package xyz.kbws.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: 全局过滤器
 */
@Slf4j
@Component
public class GlobalRequestFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String rawPath = exchange.getRequest().getURI().getRawPath();
        log.info("请求的路径是: {}", rawPath);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

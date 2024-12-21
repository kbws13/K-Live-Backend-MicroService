package xyz.kbws.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * @author kbws
 * @date 2024/12/21
 * @description: admin 模块过滤器
 */
@Slf4j
@Component
public class AdminFilter extends AbstractGatewayFilterFactory {
    @Override
    public GatewayFilter apply(Object config) {
        return (((exchange, chain) -> {
            // TODO 管理端必须要登录
            return chain.filter(exchange);
        }));
    }
}

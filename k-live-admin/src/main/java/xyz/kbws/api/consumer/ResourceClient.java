package xyz.kbws.api.consumer;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@FeignClient(name = "k-live-resource", contextId = "resourceClient")
public interface ResourceClient {
}

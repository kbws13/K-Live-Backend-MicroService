package xyz.kbws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@EnableFeignClients
@SpringBootApplication
public class KLiveWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(KLiveWebApplication.class, args);
    }
}

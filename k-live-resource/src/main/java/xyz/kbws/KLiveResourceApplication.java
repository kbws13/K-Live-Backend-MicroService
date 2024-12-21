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
public class KLiveResourceApplication {
    public static void main(String[] args) {
        SpringApplication.run(KLiveResourceApplication.class, args);
    }
}

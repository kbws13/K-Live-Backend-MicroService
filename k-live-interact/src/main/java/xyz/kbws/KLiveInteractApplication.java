package xyz.kbws;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@EnableFeignClients
@EnableScheduling
@MapperScan(value = "xyz.kbws.mapper")
@SpringBootApplication
public class KLiveInteractApplication {
    public static void main(String[] args) {
        SpringApplication.run(KLiveInteractApplication.class, args);
    }
}

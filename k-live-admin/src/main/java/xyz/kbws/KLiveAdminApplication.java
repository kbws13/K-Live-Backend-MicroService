package xyz.kbws;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@EnableFeignClients
@MapperScan(value = "xyz.kbws.mapper")
@SpringBootApplication
public class KLiveAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(KLiveAdminApplication.class, args);
    }
}

package xyz.kbws;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import xyz.kbws.es.EsComponent;

import javax.annotation.Resource;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@Component
public class AppInit implements ApplicationRunner {
    @Resource
    private EsComponent esComponent;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        esComponent.createIndex();
    }
}

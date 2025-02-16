package xyz.kbws.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

import javax.annotation.PreDestroy;

/**
 * @author kbws
 * @date 2024/12/14
 * @description:
 */
@Configuration
public class EsConfig {

    @Value("${es.username:elastic}")
    private String username;

    @Value("${es.password:elastic}")
    private String password;

    private RestHighLevelClient restHighLevelClient;

    @PreDestroy
    public void closeClient() {
        try {
            if (restHighLevelClient != null) {
                restHighLevelClient.close();
            }
        } catch (Exception e) {
            // 日志记录
            System.err.println("关闭 Elasticsearch 客户端时出错：" + e.getMessage());
        }
    }

    @Bean
    public RestHighLevelClient elasticsearchClient(AppConfig appConfig) {
        final ClientConfiguration clientConfiguration = ClientConfiguration
                .builder()
                .connectedTo(appConfig.getEsHost())
                .withBasicAuth(username, password)
                .build();
        restHighLevelClient = RestClients.create(clientConfiguration).rest();
        return restHighLevelClient;
    }
}

package xyz.kbws.config;

import org.elasticsearch.client.RestHighLevelClient;
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
                .build();
        restHighLevelClient = RestClients.create(clientConfiguration).rest();
        return restHighLevelClient;
    }
}

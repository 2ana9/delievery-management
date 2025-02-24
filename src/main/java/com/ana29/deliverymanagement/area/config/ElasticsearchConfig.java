package com.ana29.deliverymanagement.area.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

@Configuration
@EnableAutoConfiguration(exclude = {
        ElasticsearchDataAutoConfiguration.class,
        ElasticsearchRepositoriesAutoConfiguration.class
}) //엘라스틱 서치 서버가 없는경우 오류 방어
public class ElasticsearchConfig extends ElasticsearchConfiguration {
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("39.112.24.48:9200")
                .withBasicAuth("elastic", "junyoung")
                .build();
    }
}
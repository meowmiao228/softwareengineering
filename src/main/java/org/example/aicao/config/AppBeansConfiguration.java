package org.example.aicao.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties({LlmProperties.class, TeachingProperties.class})
public class AppBeansConfiguration {

    @Bean
    public RestClient llmRestClient(LlmProperties llmProperties) {
        return RestClient.builder()
                .baseUrl(llmProperties.apiBaseUrl())
                .build();
    }
}

package com.wasel.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // ⏱️ Timeout settings
        factory.setConnectTimeout(5000); // 5 ثواني للاتصال
        factory.setReadTimeout(5000);    // 5 ثواني للقراءة

        return new RestTemplate(factory);
    }
}
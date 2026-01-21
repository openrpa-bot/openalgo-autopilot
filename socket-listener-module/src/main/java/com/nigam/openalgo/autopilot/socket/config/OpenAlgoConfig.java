package com.nigam.openalgo.autopilot.socket.config;

import in.openalgo.OpenAlgo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAlgoConfig {

    @Value("${openalgo.broker.default.server.apiKey}")
    private String apiKey;

    @Value("${openalgo.broker.default.server.host}")
    private String host;

    @Value("${openalgo.broker.default.server.port}")
    private int port;

    @Value("${openalgo.broker.default.ws.url}")
    private String wsUrl;

    @Bean
    public OpenAlgo openAlgoClient() {
        return new OpenAlgo.Builder(apiKey)
                .host("http://" + host + ":" + port)
                .wsUrl(wsUrl)
                .build();
    }
}

package com.scube.document;

import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestConfig {
    @Bean
    @RestartScope
    @ServiceConnection
    public RabbitMQContainer rabbitMQContainer() {
        return new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.11.9-alpine"));
    }

    @Bean
    @RestartScope
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        var c = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.2-alpine"));
        c.withInitScript("init.sql");
        c.withUrlParam("currentSchema", "document");
        return c;
    }
}

package com.scube.document;

import com.scube.audit.EnableAuditingLibrary;
import com.scube.rabbit.core.annotation.EnableRabbitMQLibrary;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableRabbitMQLibrary(additionalPackages = "com.scube")
@EnableAuditingLibrary
@EnableJpaRepositories(basePackages = {"com.scube"})
@EntityScan(basePackages = {"com.scube"})
@OpenAPIDefinition(info = @Info(title = "Document Service API", version = "1.0", description = "Swagger Documentation"))
public class DocumentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentServiceApplication.class, args);
    }

}

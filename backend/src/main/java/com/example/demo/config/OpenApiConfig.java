package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Swagger documentation
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Golf Analytics API")
                .version("1.0.0")
                .description("RESTful API for golf shot analytics and session management. " +
                           "This API allows you to upload golf session data from various sources " +
                           "(Garmin R10, Awesome Golf) and provides comprehensive analytics.")
                .contact(new Contact()
                    .name("Golf Analytics Team")
                    .email("support@golfanalytics.com")
                    .url("https://golfanalytics.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Development server"),
                new Server()
                    .url("https://api.golfanalytics.com")
                    .description("Production server")));
    }
}
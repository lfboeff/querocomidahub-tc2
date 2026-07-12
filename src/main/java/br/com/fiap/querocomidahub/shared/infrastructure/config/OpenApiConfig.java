package br.com.fiap.querocomidahub.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Quero Comida Hub API")
                .description("""
                        Delivery management API for the Quero Comida Hub platform.
                        All error responses follow the RFC 7807 ProblemDetail format.
                        All timestamps are in UTC.
                        """)
                .version("1.0.0"));
    }

    @Bean
    public OpenApiCustomizer sortSchemasAlphabetically() {
        return openApi -> {
            var schemas = openApi.getComponents().getSchemas();
            if (schemas != null) {
                Map<String, io.swagger.v3.oas.models.media.Schema> sorted = schemas.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);
                openApi.getComponents().setSchemas(sorted);
            }
        };
    }
}

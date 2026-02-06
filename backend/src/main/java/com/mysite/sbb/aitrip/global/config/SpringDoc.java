package com.mysite.sbb.aitrip.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "AI Trip API 서버", version = "v1"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SpringDoc {

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/api/**")
                .addOpenApiCustomizer(openApi ->
                        openApi.addSecurityItem(new SecurityRequirement().addList("bearerAuth")))
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("auth")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi tripApi() {
        return GroupedOpenApi.builder()
                .group("trip")
                .pathsToMatch("/api/trips/**")
                .build();
    }

    @Bean
    public GroupedOpenApi placeApi() {
        return GroupedOpenApi.builder()
                .group("place")
                .pathsToMatch("/api/places/**")
                .build();
    }

    @Bean
    public GroupedOpenApi scheduleApi() {
        return GroupedOpenApi.builder()
                .group("schedule")
                .pathsToMatch("/api/schedules/**", "/api/trips/*/schedules/**")
                .build();
    }
}

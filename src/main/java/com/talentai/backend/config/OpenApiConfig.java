package com.talentai.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TalentAI API")
                        .version("1.0.0")
                        .description("Documentation de l'API TalentAI pour la gestion des candidats et offres d'emploi.")
                        .contact(new Contact()
                                .name("Support Technique")
                                .email("support@talentai.com")));
    }
}
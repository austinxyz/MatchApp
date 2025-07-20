package com.utr.match;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger Configuration for API documentation
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)  // Using OpenAPI 3.0 specification
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.utr.match"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * API information configuration
     * @return ApiInfo object with API details
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("UTR Match API")
                .description("API for UTR Match Analysis")
                .contact(new Contact("UTR Team", "", "support@utr.com"))
                .termsOfServiceUrl("")
                .version("1.0")
                .build();
    }
}

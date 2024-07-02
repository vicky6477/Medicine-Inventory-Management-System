package com.panda.medicineinventorymanagementsystem.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        Components components = new Components()
                .addSecuritySchemes("globalHeader", new SecurityScheme()
                        .name("global-header-name")
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .scheme("header"));
//        components.addSecuritySchemes("globalCookie", new SecurityScheme()
//                .name("global-cookie-name")
//                .type(SecurityScheme.Type.APIKEY)
//                .in(SecurityScheme.In.COOKIE)
//                .scheme("cookie"));

        components.addSecuritySchemes("globalBearer", new SecurityScheme()
                .name("global-bearer-name")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer"));


        return new OpenAPI()
                .info(new Info()
                        .title("My Spring 3 OPENAPI DEMO APP")
                        .description("This is a demo app for using swagger 3 openapi")
                        .version("1.0.0"))
//                .path("/test", getPathItem())
                .components(components)
                .security(List.of(new SecurityRequirement().addList("globalHeader"),
                        new SecurityRequirement().addList("globalBearer")
                ));
    }

    private PathItem getPathItem() {
        PathItem pathItem = new PathItem();
        Operation operation = new Operation();
        operation.addParametersItem(new Parameter()
                .in("header")
                .name("controller-header")
                .schema(new Schema()
                        ._default("default application Key")
                        .name("localHeader")
                        .type("string"))
                .description("key for application to pass API Gateway"));

//        operation.addParametersItem(new Parameter()
//                .in("cookie")
//                .name("controller-session")
//                .schema(new Schema()._default("controller cookie session ID").name("schema name").type("string"))
//                .description("controller session ID for user service"));
//        pathItem.get(operation);
        return pathItem;
    }

//    private SecurityScheme getScheme(String headerKey, String scheme) {
//        return new SecurityScheme()
//                .name(headerKey)
//                .type(SecurityScheme.Type.APIKEY)
//                .in(SecurityScheme.In.HEADER)
//                .scheme("header");
//    }
}

package com.nigam.openalgo.autopilot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${common.host}")
    private String commonHost;

    @Value("${ui.port}")
    private String uiPort;

    @Bean
    public OpenAPI openAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://" + commonHost + ":" + uiPort);
        devServer.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setName("OpenAlgo Autopilot");
        contact.setEmail("support@openalgo.com");

        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html");

        Info info = new Info()
                .title("OpenAlgo Autopilot API")
                .version("1.0.0-SNAPSHOT")
                .contact(contact)
                .description("REST API for OpenAlgo Autopilot Application")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}

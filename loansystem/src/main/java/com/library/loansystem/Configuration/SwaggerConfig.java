package com.library.loansystem.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Book Loan System",
                version = "1.0",
                description = "API for managing a library system with book loans, including books, publishers, authors, and users. \n\n" +
                        "This project is a work in progress exploring RESTful API design and Spring Boot development. \n\n" +
                        "CRUD operations for books, publishers, and authors are already implemented. \n\n" +
                        "Loan functionality and user management are currently under development."
        ),
        servers = {
                @Server(
                        description = "Local server",
                        url = "http://localhost:8080"
                )
        }
)
public class SwaggerConfig {

}

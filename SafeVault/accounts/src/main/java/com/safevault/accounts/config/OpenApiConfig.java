package com.safevault.accounts.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Account Service",
                version = "v1"
        ),
        servers = {
                @Server(
                        url = "${swagger.server.url}",
                        description = "Account Service"
                )
        }
)
public class OpenApiConfig {}

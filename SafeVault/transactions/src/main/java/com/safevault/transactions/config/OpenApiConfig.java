package com.safevault.transactions.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Transaction Service",
                version = "v1"
        ),
        servers = {
                @Server(
                        url = "${swagger.server.url}",
                        description = "Transaction Service"
                )
        }
)
public class OpenApiConfig {}

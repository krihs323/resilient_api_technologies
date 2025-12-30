package com.example.resilient_api.infrastructure.documentation;

import com.example.resilient_api.infrastructure.entrypoints.handler.TechnologyHandlerImpl;
import org.springframework.context.annotation.Bean;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


//public class TechnologyRouter {
//    @Bean
//    public RouterFunction<ServerResponse> route(TechnologyHandlerImpl technologyHandler) {
//        return route()
//                .POST("/user", technologyHandler::createTechnology, spec -> spec
//                        .operation(op -> op
//                                .operationId("createUser")
//                                .summary("Crear nuevo usuario")
//                                .description("Registra un nuevo usuario.")
//                                // Define el cuerpo de la petición
//                                .requestBody(req -> req.content(content -> content.schema(schema -> schema.implementation(UserDTO.class))))
//                                // Define las respuestas
//                                .response(201, res -> res.description("Usuario creado exitosamente"))
//                                .response(400, res -> res.description("Datos de entrada inválidos"))
//                        )
//                ).build();
//    }
//}

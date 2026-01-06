package com.example.resilient_api.infrastructure.entrypoints;

import com.example.resilient_api.infrastructure.entrypoints.dto.TechnologyDTO;
import com.example.resilient_api.infrastructure.entrypoints.handler.TechnologyHandlerImpl;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterRest {

    @RouterOperations({ @RouterOperation(path = "/getAllPersons", beanClass = TechnologyDTO.class, beanMethod = "getAll"),
            @RouterOperation(path = "/getPerson/{id}", beanClass = TechnologyDTO.class, beanMethod = "getById"),
            @RouterOperation(path = "/createPerson", beanClass = TechnologyDTO.class, beanMethod = "save"),
            @RouterOperation(path = "/deletePerson/{id}", beanClass = TechnologyDTO.class, beanMethod = "delete") })
    @Bean
    public RouterFunction<ServerResponse> routerFunction(TechnologyHandlerImpl technologyHandler) {
        return SpringdocRouteBuilder.route()
                .POST("/technology",
                        technologyHandler::createTechnology,
                        ops -> ops.beanClass(TechnologyHandlerImpl.class).beanMethod("createTechnology"))
                .POST("/technology/savecapacities",
                        technologyHandler::createCapacities,
                        ops -> ops.beanClass(TechnologyHandlerImpl.class).beanMethod("createCapacities"))
                .GET("/capacity/capacities-by-Bootcamps/",
                        technologyHandler::listTecnologyByCapacity,
                        ops -> ops.beanClass(TechnologyHandlerImpl.class).beanMethod("listTecnologyByCapacity")
                )
                .DELETE("/technology/{idBootcamp}",
                        technologyHandler::deleteCapacityByBootcamp,
                        ops -> ops.beanClass(TechnologyHandlerImpl.class).beanMethod("deleteCapacityByBootcamp")
                )
                .GET("/technology/capacity",
                        technologyHandler::listTechnologyCapacities,
                        ops -> ops.beanClass(TechnologyHandlerImpl.class).beanMethod("listTechnologyCapacities")
                )
                .build();
    }
}

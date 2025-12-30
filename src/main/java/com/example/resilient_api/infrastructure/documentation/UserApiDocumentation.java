/*
package com.example.resilient_api.infrastructure.documentation;

import com.example.resilient_api.infrastructure.entrypoints.dto.TechnologyDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestión de usuarios")
public interface UserApiDocumentation {

    @Operation(
            operationId = "createTechnology", // ID de la operación
            summary = "Crear nuevo usuario",
            description = "Registra un nuevo usuario en el sistema.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = TechnologyDTO.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
        // El método no necesita implementación, solo la firma.
    Mono<ServerResponse> createTechnology(ServerRequest request);
}
*/

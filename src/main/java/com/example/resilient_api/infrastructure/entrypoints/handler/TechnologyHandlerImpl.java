package com.example.resilient_api.infrastructure.entrypoints.handler;

import com.example.resilient_api.domain.api.TechnologyServicePort;
import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.exceptions.CustomException;
import com.example.resilient_api.domain.exceptions.TechnicalException;
import com.example.resilient_api.domain.model.CapacityTechnology;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.CapacityTechnologyEntity;
import com.example.resilient_api.infrastructure.entrypoints.dto.CacacitiesDTO;
import com.example.resilient_api.infrastructure.entrypoints.dto.CapacityTechnologyDTO;
import com.example.resilient_api.infrastructure.entrypoints.dto.TechnologyDTO;
import com.example.resilient_api.infrastructure.entrypoints.mapper.CapacityTechnologyMapper;
import com.example.resilient_api.infrastructure.entrypoints.mapper.TechnologyMapper;
import com.example.resilient_api.infrastructure.entrypoints.util.APIResponse;
import com.example.resilient_api.infrastructure.entrypoints.util.ErrorDTO;
import com.example.resilient_api.infrastructure.validation.ObjectValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.resilient_api.infrastructure.entrypoints.util.Constants.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class TechnologyHandlerImpl {

    private final TechnologyServicePort technologyServicePort;
    private final TechnologyMapper technologyMapper;
    private final ObjectValidator objectValidator;
    private final CapacityTechnologyMapper capacityTechnologyMapper;

    public Mono<ServerResponse> createTechnology(ServerRequest request) {
        String messageId = getMessageId(request);
        return request.bodyToMono(TechnologyDTO.class).flatMap(objectValidator::validate)
                .flatMap(technology -> technologyServicePort.registerTechnology(technologyMapper.technologyDTOToTechnology(technology), messageId)
                        .doOnSuccess(savedTechnology -> log.info("Technology created successfully with messageId: {}", messageId))
                )
                .flatMap(savedTechnology -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(TechnicalMessage.TECHNOLOGY_CREATED.getMessage()))
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .doOnError(ex -> log.error(TECHNOLOGY_ERROR, ex))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        TechnicalMessage.INVALID_PARAMETERS,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(CustomException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        TechnicalMessage.INVALID_REQUEST,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.INVALID_REQUEST.getCode())
                                .message(ex.getMessage())
                                .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error occurred creating technoiloy for messageId: {}", messageId, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }

    @Operation(
            summary = "Registrar tecnologias para la capacidad registrada",
            description = "Asocia las tecnologias para la capacidad registrada",
            requestBody = @RequestBody(
                    description = "Información de la capacidad a registrar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CacacitiesDTO.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Capacidad creada exitosamente"),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            })
    public Mono<ServerResponse> createCapacities(ServerRequest request) {
        String messageId = getMessageId(request);
        return request.bodyToFlux(CapacityTechnologyDTO.class) // Recibe la lista como Flux
                .collectList()
                .flatMap(capacities -> {
                    List<CapacityTechnology> capacityTechnologyList = capacities.stream().map(capacityTechnologyMapper::capacityTechnologyDTOToCapacityTechnology)
                            .toList();
                    return technologyServicePort.registerCapcities(capacityTechnologyList, messageId);
                })
                .flatMap(savedTechnology -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(TechnicalMessage.TECHNOLOGY_CREATED.getMessage()))
                .contextWrite(Context.of(X_MESSAGE_ID, messageId))
                .doOnError(ex -> log.error(TECHNOLOGY_ERROR, ex))
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        TechnicalMessage.INVALID_PARAMETERS,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(TechnicalException.class, ex -> buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        messageId,
                        TechnicalMessage.INTERNAL_ERROR,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(CustomException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        TechnicalMessage.INVALID_REQUEST,
                        List.of(ErrorDTO.builder()
                                .code(TechnicalMessage.INVALID_REQUEST.getCode())
                                .message(ex.getMessage())
                                .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error occurred for messageId: {}", messageId, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }

    @Operation(parameters = {
            @Parameter(name = "idCapacity", in = ParameterIn.QUERY, example = "1", description = "id de la capacidad")
    })
    public Mono<ServerResponse> listTecnologyByCapacity(ServerRequest request) {
        String messageId = getMessageId(request);
        String idCapacityStr = request.queryParam("idCapacity").orElse("0");
        Long idBootcamp = Long.parseLong(idCapacityStr);
        Flux<TechnologyDTO> resultMono = technologyServicePort.listTechnologyByCapacity(idBootcamp, messageId).map(technologyMapper::toDTO);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(resultMono, TechnologyDTO.class);
    }

    @Operation(
            summary = "Borrar tecnologías por capacidad",
            parameters = {
                    @Parameter(name = "idBootcamp", in = ParameterIn.QUERY, example = "1", description = "ID del bootcamp de referencia")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Lista de capacidades y tecnologías a borrar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CapacityTechnology.class))
            )
    )
    public Mono<ServerResponse> deleteCapacityByBootcamp(ServerRequest request) {
        String messageId = getMessageId(request);
        int idBootcamp = Integer.parseInt(request.pathVariable("idBootcamp"));
        return request.bodyToFlux(CapacityTechnology.class)
                .collectList()
                .flatMap(capacityList -> {
                    log.info("Cuerpo recibido para messageId {}: {}", messageId, capacityList);
                    // Validación: Si la lista está vacía, respondemos de inmediato
                    if (capacityList.isEmpty()) {
                        return ServerResponse.badRequest()
                                .bodyValue(Map.of("error", "La lista de capacidades no puede estar vacía"));
                    }
                    log.info("Iniciando borrado de {} capacidades. MessageId: {} Bootcamp {}", capacityList.size(), messageId, idBootcamp);
                    // Ejecutamos el servicio de borrado
                    return technologyServicePort.deleteTechnologyByCapacity(capacityList, messageId)
                            .then(ServerResponse.noContent().build()); // 204 No Content al terminar
                })
                .onErrorResume(BusinessException.class, ex -> buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        messageId,
                        TechnicalMessage.TECHNOLOGY_WITH_OTHER_CAPACITIES,
                        List.of(ErrorDTO.builder()
                                .code(ex.getTechnicalMessage().getCode())
                                .message(ex.getTechnicalMessage().getMessage())
                                .param(ex.getTechnicalMessage().getParam())
                                .build())))
                .onErrorResume(ex -> {
                    log.error("Unexpected error occurred for messageId: {}", messageId, ex);
                    return buildErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            messageId,
                            TechnicalMessage.INTERNAL_ERROR,
                            List.of(ErrorDTO.builder()
                                    .code(TechnicalMessage.INTERNAL_ERROR.getCode())
                                    .message(TechnicalMessage.INTERNAL_ERROR.getMessage())
                                    .build()));
                });
    }

    @Operation(parameters = {
            @Parameter(name = "page", in = ParameterIn.QUERY, example = "0", description = "Número de página"),
            @Parameter(name = "size", in = ParameterIn.QUERY, example = "10", description = "Tamaño de la pàgina"),
            @Parameter(name = "sortBy", in = ParameterIn.QUERY, example = "name", description = "Ordenar por"),
            @Parameter(name = "sortDir", in = ParameterIn.QUERY, example = "ASC", description = "Dirección ASC/DESC")
    })
    public Mono<ServerResponse> listTechnologyCapacities(ServerRequest request) {
        String messageId = getMessageId(request);
        //Parametros de paginacion
        String pageStr = request.queryParam("page").orElse("0");
        int page = Integer.parseInt(pageStr);
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String sortBy = request.queryParam("sortBy").orElse("name");
        String sortDir = request.queryParam("sortDir").orElse("ASC");
        Flux<CapacityTechnologyDTO> resultMono = technologyServicePort.listTechnologiesCapacity(page,  size,  sortBy,  sortDir, messageId).map(capacityTechnologyMapper::capacityTechnologyToCapacityTechnologyDTO);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(resultMono, CapacityTechnologyDTO.class);
    }

    private Mono<ServerResponse> buildErrorResponse(HttpStatus httpStatus, String identifier, TechnicalMessage error,
                                                    List<ErrorDTO> errors) {
        return Mono.defer(() -> {
            APIResponse apiErrorResponse = APIResponse
                    .builder()
                    .code(error.getCode())
                    .message(error.getMessage())
                    .identifier(identifier)
                    .date(Instant.now().toString())
                    .errors(errors)
                    .build();
            return ServerResponse.status(httpStatus)
                    .bodyValue(apiErrorResponse);
        });
    }

    private String getMessageId(ServerRequest serverRequest) {
        return serverRequest.headers().firstHeader(X_MESSAGE_ID);
    }
}

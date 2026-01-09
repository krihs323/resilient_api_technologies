package com.example.resilient_api.infrastructure.entrypoints.handler;

import com.example.resilient_api.domain.api.TechnologyServicePort;
import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.model.CapacityTechnology;
import com.example.resilient_api.domain.model.Technology;
import com.example.resilient_api.infrastructure.entrypoints.dto.CapacityTechnologyDTO;
import com.example.resilient_api.infrastructure.entrypoints.dto.TechnologyDTO;
import com.example.resilient_api.infrastructure.entrypoints.mapper.CapacityTechnologyMapper;
import com.example.resilient_api.infrastructure.entrypoints.mapper.TechnologyMapper;
import com.example.resilient_api.infrastructure.validation.ObjectValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnologyHandlerImplTest {

    @Mock
    private TechnologyServicePort technologyServicePort;
    @Mock
    private TechnologyMapper technologyMapper;
    @Mock
    private ObjectValidator objectValidator;
    @Mock
    private CapacityTechnologyMapper capacityTechnologyMapper;

    @InjectMocks
    private TechnologyHandlerImpl technologyHandler;

    private ServerRequest serverRequest;

    @BeforeEach
    void setUp() {
        // Mock básico del Header para messageId
        serverRequest = mock(ServerRequest.class);
        ServerRequest.Headers headers = mock(ServerRequest.Headers.class);
        when(serverRequest.headers()).thenReturn(headers);
        when(headers.firstHeader(anyString())).thenReturn("test-message-id");
    }

    @Test
    @DisplayName("Debe crear una tecnología exitosamente (201 Created)")
    void createTechnologySuccess() {
        // GIVEN
        TechnologyDTO dto = new TechnologyDTO(1L, "java", "descripcion"); // Asume que tienes este DTO con datos
        Technology model =  new Technology(1L, "java", "descripcion");

        when(serverRequest.bodyToMono(TechnologyDTO.class)).thenReturn(Mono.just(dto));
        when(technologyMapper.technologyDTOToTechnology(dto)).thenReturn(model);
        when(technologyServicePort.registerTechnology(eq(model), anyString()))
                .thenReturn(Mono.just(model));

        // WHEN
        Mono<ServerResponse> responseMono = technologyHandler.createTechnology(serverRequest);

        // THEN
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.CREATED, response.statusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar 400 cuando ocurre una BusinessException")
    void createTechnologyBusinessError() {
        // GIVEN
        TechnologyDTO dto = new TechnologyDTO(1L, "java", "descripcion");
        when(serverRequest.bodyToMono(TechnologyDTO.class)).thenReturn(Mono.just(dto));

        // Simulamos el error de negocio
        BusinessException businessException = new BusinessException(TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS);
        when(technologyServicePort.registerTechnology(any(), anyString()))
                .thenReturn(Mono.error(businessException));

        // WHEN
        Mono<ServerResponse> responseMono = technologyHandler.createTechnology(serverRequest);

        // THEN
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.BAD_REQUEST, response.statusCode());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe listar tecnologías por capacidad exitosamente")
    void listTecnologyByCapacitySuccess() {
        // GIVEN
        String messageId = "test-message-id";
        Long idCapacity = 1L;

        // Mock de los parámetros de consulta
        when(serverRequest.queryParam("idCapacity")).thenReturn(Optional.of("1"));

        // Preparar datos de prueba
        Technology model = new Technology(1L, "java", "descripcion"); // Asegúrate de tener constructor o Builder
        TechnologyDTO dto = new TechnologyDTO(1L, "java", "descripcion");

        // Stubbing de los servicios (Asegúrate de que los tipos coincidan: Long y String)
        when(technologyServicePort.listTechnologyByCapacity(idCapacity, messageId))
                .thenReturn(Flux.just(model));
     //   when(technologyMapper.toDTO(model)).thenReturn(dto);

        // WHEN
        Mono<ServerResponse> responseMono = technologyHandler.listTecnologyByCapacity(serverRequest);

        // THEN
        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.statusCode());
                    assertEquals(MediaType.APPLICATION_JSON, response.headers().getContentType());
                })
                .verifyComplete();

        // Verificación opcional de que se llamaron los métodos
        verify(technologyServicePort).listTechnologyByCapacity(eq(idCapacity), anyString());
    }


}
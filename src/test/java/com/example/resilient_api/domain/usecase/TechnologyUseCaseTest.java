package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.model.CapacityTechnology;
import com.example.resilient_api.domain.model.Technology;
import com.example.resilient_api.domain.spi.CapacityTechnologyPersistencePort;
import com.example.resilient_api.domain.spi.TechnologyPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnologyUseCaseTest {

    @Mock
    private TechnologyPersistencePort technologyPersistencePort;

    @Mock
    private CapacityTechnologyPersistencePort capacityTechnologyPersistencePort;

    @InjectMocks
    private TechnologyUseCase technologyUseCase;

    private Technology technology;
    private String messageId = "test-uuid";
    private List<CapacityTechnology> list;

    @BeforeEach
    void setUp() {
        technology = new Technology(1L, "Java", "Programming Language");

        list = List.of(new CapacityTechnology(1L, 1L, 1L));
    }

    @Test
    @DisplayName("Should register technology when it does not exist")
    void registerTechnologySuccess() {
        when(technologyPersistencePort.existByName(anyString())).thenReturn(Mono.just(false));
        when(technologyPersistencePort.save(any(Technology.class))).thenReturn(Mono.just(technology));

        Mono<Technology> result = technologyUseCase.registerTechnology(technology, messageId);

        StepVerifier.create(result)
                .expectNext(technology)
                .verifyComplete();

        verify(technologyPersistencePort).existByName(technology.name());
    }

    @Test
    @DisplayName("Should throw BusinessException when technology already exists")
    void registerTechnologyAlreadyExists() {
        when(technologyPersistencePort.existByName(anyString())).thenReturn(Mono.just(true));

        Mono<Technology> result = technologyUseCase.registerTechnology(technology, messageId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS)
                .verify();
    }

    @Test
    @DisplayName("Should delete technology and capacity relations when not used elsewhere")
    void deleteTechnologyByCapacitySuccess() {

        when(technologyPersistencePort.getTechnologiesInOtherCapacities(anyList(), anyString()))
                .thenReturn(Mono.just(false));
        when(technologyPersistencePort.deleteTechnologyByCapacity(anyList(), anyString()))
                .thenReturn(Mono.empty());
        when(capacityTechnologyPersistencePort.deleteAllCapacityTechnologies(anyList(), anyString()))
                .thenReturn(Mono.empty());

        Mono<Void> result = technologyUseCase.deleteTechnologyByCapacity(list, messageId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(technologyPersistencePort).deleteTechnologyByCapacity(list, messageId);
        verify(capacityTechnologyPersistencePort).deleteAllCapacityTechnologies(list, messageId);
    }

    @Test
    @DisplayName("Should throw exception when technology is linked to other capacities")
    void deleteTechnologyByCapacityFailure() {

        when(technologyPersistencePort.getTechnologiesInOtherCapacities(anyList(), anyString()))
                .thenReturn(Mono.just(true));

        Mono<Void> result = technologyUseCase.deleteTechnologyByCapacity(list, messageId);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getTechnicalMessage() == TechnicalMessage.TECHNOLOGY_WITH_OTHER_CAPACITIES)
                .verify();

        verify(technologyPersistencePort, never()).deleteTechnologyByCapacity(any(), any());
    }

    @Test
    void registerCapcities() {
        when(capacityTechnologyPersistencePort.saveAll(anyList(), anyString()))
                .thenReturn(Mono.empty());

        Mono<Void> result = technologyUseCase.registerCapcities(list, messageId);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should list technologies by capacity")
    void listTechnologyByCapacity() {
        when(technologyPersistencePort.getTecnologiesByCapacity(anyLong(), anyString()))
                .thenReturn(Flux.just(technology));

        Flux<Technology> result = technologyUseCase.listTechnologyByCapacity(1L, messageId);

        StepVerifier.create(result)
                .expectNext(technology)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should list technologies capacity")
    void listTechnologiesCapacity() {

        int page = 1;
        int size = 10;
        String sortBy = "name";
        String sortDir = "ASC";

        CapacityTechnology capacityTechnology = new CapacityTechnology(1L, 1L, 1L);

        when(capacityTechnologyPersistencePort.getlAll(anyInt(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(Flux.just(capacityTechnology));

        Flux<CapacityTechnology> result = technologyUseCase.listTechnologiesCapacity(page, size, sortBy, sortDir, messageId);

        StepVerifier.create(result)
                .expectNext(capacityTechnology)
                .verifyComplete();
    }
}
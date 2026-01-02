package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.CapacityTechnology;
import com.example.resilient_api.domain.model.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyPersistencePort {
    Mono<Technology> save(Technology technology);
    Mono<Boolean> existByName(String name);
    Flux<Technology> getTecnologiesByCapacity(Long idBootcamp, String messageId);
    Mono<Boolean> getTechnologiesInOtherCapacities(List<CapacityTechnology> capacityTechnologies, String messageId);
    Mono<Void> deleteTechnologyByCapacity(List<CapacityTechnology> capacityTechnologies, String messageId);
}

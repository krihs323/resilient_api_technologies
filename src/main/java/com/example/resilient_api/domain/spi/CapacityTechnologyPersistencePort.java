package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.CapacityTechnology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.List;

public interface CapacityTechnologyPersistencePort {
    Mono<Void> deleteAllCapacityTechnologies(List<CapacityTechnology> capacityTechnologies, String messageId);
    Mono<Void> saveAll(List<CapacityTechnology> capacityTechnologyList, String messageId);
    Flux<CapacityTechnology> getlAll(int page, int size, String sortBy, String sortDir, String messageId);
}

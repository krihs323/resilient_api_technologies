package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.Technology;
import reactor.core.publisher.Mono;

public interface TechnologyPersistencePort {
    Mono<Technology> save(Technology technology);
    Mono<Boolean> existByName(String name);
}

package com.example.resilient_api.domain.api;

import com.example.resilient_api.domain.model.CapacityTechnology;
import com.example.resilient_api.domain.model.Technology;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TechnologyServicePort {
    Mono<Technology> registerTechnology(Technology technology, String messageId);
    Flux<Technology> listTechnologyByCapacity(Long idBootcamp, String messageId);
    Mono<Void> deleteTechnologyByCapacity(List<CapacityTechnology> capacityTechnologies, String messageId);
}

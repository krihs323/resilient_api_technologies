package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.CapacityTechnology;
import reactor.core.publisher.Mono;


import java.util.List;

public interface CapacityTechnologyPersistencePort {
    Mono<Void> deleteAllCapacityTechnologies(List<CapacityTechnology> capacityTechnologies, String messageId);
}

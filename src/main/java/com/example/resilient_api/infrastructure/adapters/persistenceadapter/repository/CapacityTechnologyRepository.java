package com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository;

import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.CapacityTechnologyEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CapacityTechnologyRepository extends ReactiveCrudRepository<CapacityTechnologyEntity, Long> {

    @Query("""
            SELECT COUNT(DISTINCT c.id)
                    FROM capacities c
                    INNER JOIN capacities_x_tecnologies ct
                        ON c.id = ct.id_capacity
        """)
    Mono<Long> countGroupedCapacities();
}
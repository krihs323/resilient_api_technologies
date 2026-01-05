package com.example.resilient_api.infrastructure.adapters.persistenceadapter;

import com.example.resilient_api.domain.model.CapacityTechnology;
import com.example.resilient_api.domain.spi.CapacityTechnologyPersistencePort;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class CapacityTechnologyPersistenceAdapter implements CapacityTechnologyPersistencePort {
    private final DatabaseClient databaseClient;


    @Override
    public Mono<Void> deleteAllCapacityTechnologies(List<CapacityTechnology> capacityTechnologies, String messageId) {
        // 1. Validación inicial
        if (capacityTechnologies == null || capacityTechnologies.isEmpty()) {
            log.warn("No capacity IDs provided for deletion, messageId: {}", messageId);
            return Mono.empty();
        }

        // 2. Formatear los IDs: "1,2,3"
        String idsFormatted = capacityTechnologies.stream()
                .map(capacity -> String.valueOf(capacity.idCapacity()))
                .collect(Collectors.joining(","));

        // 3. Preparar el SQL
        String sql = "DELETE FROM tecnologias.capacities_x_tecnologies WHERE id_capacity IN (%s)"
                .formatted(idsFormatted);

        log.info("Executing delete for capacity IDs: [{}] for messageId: {}", idsFormatted, messageId);

        // 4. Ejecutar y retornar Mono<Void>
        return databaseClient.sql(sql)
                .fetch()
                .rowsUpdated() // Retorna la cantidad de filas afectadas (Mono<Long>)
                .doOnNext(rows -> log.info("Successfully deleted {} rows for messageId: {}", rows, messageId))
                .then(); // Transformamos el Mono<Long> en Mono<Void>
    }
}

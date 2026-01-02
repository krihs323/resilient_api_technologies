package com.example.resilient_api.infrastructure.adapters.persistenceadapter;

import com.example.resilient_api.domain.model.CapacityTechnology;
import com.example.resilient_api.domain.model.Technology;
import com.example.resilient_api.domain.spi.TechnologyPersistencePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.TechnologyEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.TechnologyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
public class TechnologyPersistenceAdapter implements TechnologyPersistencePort {
    private final TechnologyRepository technologyRepository;
    private final TechnologyEntityMapper technologyEntityMapper;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<Technology> save(Technology technology) {
        return technologyRepository.save(technologyEntityMapper.toEntity(technology))
                .map(technologyEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existByName(String name) {
        return technologyRepository.findByName(name)
                .map(technologyEntityMapper::toModel)
                .map(technology -> true)  // Si encuentra el usuario, devuelve true
                .defaultIfEmpty(false);  // Si no encuentra, devuelve false
    }

    @Override
    public Flux<Technology> getTecnologiesByCapacity(Long idCapacity, String messageId) {
        String sql = """
            select technologies.id as id_tecnology, technologies.name as name, technologies.description as description from capacities_x_tecnologies inner join technologies on
            capacities_x_tecnologies.id_tecnology  = technologies.id
            where capacities_x_tecnologies.id_capacity = %s
            ORDER BY NAME ASC
            """.formatted(idCapacity);
        return databaseClient.sql(sql)
                .map((row, meta) -> new Technology(
                        row.get("id_tecnology", Long.class),
                        row.get("name", String.class),
                        row.get("description", String.class)
                ))
                .all();
    }

    @Override
    public Mono<Boolean> getTechnologiesInOtherCapacities(List<CapacityTechnology> capacityTechnologies, String messageId) {

        if (capacityTechnologies == null || capacityTechnologies.isEmpty()) {
            return Mono.just(false);
        }

        String idsFormatted = capacityTechnologies.stream()
                .map(cap -> String.valueOf(cap.id_capacity()))
                .collect(Collectors.joining(","));

        String sql = """
                select id from tecnologias.capacities_x_tecnologies where id_capacity not in(%s)
                	and id_tecnology in (select id_tecnology from tecnologias.capacities_x_tecnologies where id_capacity in (%s)
                group by id_tecnology) limit 1
            """.formatted(idsFormatted, idsFormatted);
        return databaseClient.sql(sql)
                .map((row, meta) -> true)
                .first()
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Void> deleteTechnologyByCapacity(List<CapacityTechnology> capacityTechnologies, String messageId) {

        // 1. Validación inicial
        if (capacityTechnologies == null || capacityTechnologies.isEmpty()) {
            log.warn("No capacity IDs provided for deletion, messageId: {}", messageId);
            return Mono.empty();
        }

        // 2. Formatear los IDs: "1,2,3"
        String idsFormatted = capacityTechnologies.stream()
                .map(capacity -> String.valueOf(capacity.id_capacity()))
                .collect(Collectors.joining(","));

        // 3. Preparar el SQL
        String sql = """
                DELETE FROM tecnologias.technologies WHERE id in (
                select id_tecnology from tecnologias.capacities_x_tecnologies where id_capacity in (%s)
                group by id_tecnology)
                """
                .formatted(idsFormatted);

        log.info("Executing delete for capacity IDs: [{}] for messageId: {}", idsFormatted, messageId);

        log.info(sql);

        // 4. Ejecutar y retornar Mono<Void>
        return databaseClient.sql(sql)
                .fetch()
                .rowsUpdated() // Retorna la cantidad de filas afectadas (Mono<Long>)
                .doOnNext(rows -> log.info("Successfully deleted {} rows for messageId: {}", rows, messageId))
                .then(); // Transformamos el Mono<Long> en Mono<Void>
    }

}

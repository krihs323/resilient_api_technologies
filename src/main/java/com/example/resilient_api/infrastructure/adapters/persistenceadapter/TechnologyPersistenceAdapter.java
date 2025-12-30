package com.example.resilient_api.infrastructure.adapters.persistenceadapter;

import com.example.resilient_api.domain.model.Technology;
import com.example.resilient_api.domain.spi.TechnologyPersistencePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.TechnologyEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.TechnologyRepository;
import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@AllArgsConstructor
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
            """.formatted(idCapacity);;
        return databaseClient.sql(sql)
                .map((row, meta) -> new Technology(
                        row.get("id_tecnology", Long.class),
                        row.get("name", String.class),
                        row.get("description", String.class)
                ))
                .all();
    }

}

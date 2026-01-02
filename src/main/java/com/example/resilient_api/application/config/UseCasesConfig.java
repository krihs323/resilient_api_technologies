package com.example.resilient_api.application.config;

import com.example.resilient_api.domain.spi.CapacityTechnologyPersistencePort;
import com.example.resilient_api.domain.spi.EmailValidatorGateway;
import com.example.resilient_api.domain.spi.TechnologyPersistencePort;
import com.example.resilient_api.domain.usecase.TechnologyUseCase;
import com.example.resilient_api.domain.api.TechnologyServicePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.CapacityTechnologyPersistenceAdapter;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.TechnologyPersistenceAdapter;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.TechnologyEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
        private final TechnologyRepository technologyRepository;
        private final TechnologyEntityMapper technologyEntityMapper;
        private final DatabaseClient databaseClient;

        @Bean
        public TechnologyPersistencePort technologiesPersistencePort() {
                return new TechnologyPersistenceAdapter(technologyRepository, technologyEntityMapper, databaseClient);
        }

        @Bean
        public CapacityTechnologyPersistencePort capacityTechnologyPersistencePort() {
            return new CapacityTechnologyPersistenceAdapter(databaseClient);
        }

        @Bean
        public TechnologyServicePort technologiesServicePort(TechnologyPersistencePort technologiesPersistencePort, EmailValidatorGateway emailValidatorGateway, CapacityTechnologyPersistencePort capacityTechnologyPersistencePort){
                return new TechnologyUseCase(technologiesPersistencePort, emailValidatorGateway, capacityTechnologyPersistencePort);
        }
}

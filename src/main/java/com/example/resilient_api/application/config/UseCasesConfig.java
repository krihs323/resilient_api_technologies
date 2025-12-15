package com.example.resilient_api.application.config;

import com.example.resilient_api.domain.spi.EmailValidatorGateway;
import com.example.resilient_api.domain.spi.TechnologyPersistencePort;
import com.example.resilient_api.domain.usecase.TechnologyUseCase;
import com.example.resilient_api.domain.api.TechnologyServicePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.TechnologyPersistenceAdapter;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.TechnologyEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
        private final TechnologyRepository technologyRepository;
        private final TechnologyEntityMapper technologyEntityMapper;

        @Bean
        public TechnologyPersistencePort technologiesPersistencePort() {
                return new TechnologyPersistenceAdapter(technologyRepository, technologyEntityMapper);
        }

        @Bean
        public TechnologyServicePort technologiesServicePort(TechnologyPersistencePort technologiesPersistencePort, EmailValidatorGateway emailValidatorGateway){
                return new TechnologyUseCase(technologiesPersistencePort, emailValidatorGateway);
        }
}

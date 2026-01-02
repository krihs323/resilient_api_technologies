package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.model.CapacityTechnology;
import com.example.resilient_api.domain.model.Technology;
import com.example.resilient_api.domain.spi.CapacityTechnologyPersistencePort;
import com.example.resilient_api.domain.spi.EmailValidatorGateway;
import com.example.resilient_api.domain.spi.TechnologyPersistencePort;
import com.example.resilient_api.domain.api.TechnologyServicePort;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class TechnologyUseCase implements TechnologyServicePort {

    private final TechnologyPersistencePort technologyPersistencePort;
    private final EmailValidatorGateway validatorGateway;
    private final CapacityTechnologyPersistencePort capacityTechnologyPersistencePort;


    public TechnologyUseCase(TechnologyPersistencePort technologyPersistencePort, EmailValidatorGateway validatorGateway, CapacityTechnologyPersistencePort capacityTechnologyPersistencePort) {
        this.technologyPersistencePort = technologyPersistencePort;
        this.validatorGateway = validatorGateway;
        this.capacityTechnologyPersistencePort = capacityTechnologyPersistencePort;
    }

    @Override
    public Mono<Technology> registerTechnology(Technology technology, String messageId) {
        return technologyPersistencePort.existByName(technology.name())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_ALREADY_EXISTS)))
                //.flatMap(exists -> validateDescription(technology.name(), messageId))
                .flatMap(x-> technologyPersistencePort.save(technology))
//                .flatMap(validationResult -> validationResult.deliverability().equals(Constants.DELIVERABLE)
//                        ? technologyPersistencePort.save(technology)
//                        : Mono.error(new BusinessException(TechnicalMessage.INVALID_EMAIL)))
                ;
    }

    @Override
    public Flux<Technology> listTechnologyByCapacity(Long idBootcamp, String messageId) {
        return technologyPersistencePort.getTecnologiesByCapacity(idBootcamp, messageId);
    }

    @Override
    @Transactional
    public Mono<Void> deleteTechnologyByCapacity(List<CapacityTechnology> capacityTechnologies, String messageId) {
        //Valida si no esta relacionado a otras capacidades y luego borra bajo una transaccin
        return technologyPersistencePort.getTechnologiesInOtherCapacities(capacityTechnologies, messageId)
                .flatMap(existe -> {
                    if (existe){
                        return Mono.error(new BusinessException(TechnicalMessage.TECHNOLOGY_WITH_OTHER_CAPACITIES));
                    }
                    return technologyPersistencePort.deleteTechnologyByCapacity(capacityTechnologies, messageId)
                    .then(capacityTechnologyPersistencePort.deleteAllCapacityTechnologies(capacityTechnologies, messageId));
                });
    }
}

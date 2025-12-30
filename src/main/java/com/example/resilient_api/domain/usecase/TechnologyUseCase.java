package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.constants.Constants;
import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.model.Technology;
import com.example.resilient_api.domain.model.EmailValidationResult;
import com.example.resilient_api.domain.spi.EmailValidatorGateway;
import com.example.resilient_api.domain.spi.TechnologyPersistencePort;
import com.example.resilient_api.domain.api.TechnologyServicePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TechnologyUseCase implements TechnologyServicePort {

    private final TechnologyPersistencePort technologyPersistencePort;
    private final EmailValidatorGateway validatorGateway;

    public TechnologyUseCase(TechnologyPersistencePort technologyPersistencePort, EmailValidatorGateway validatorGateway) {
        this.technologyPersistencePort = technologyPersistencePort;
        this.validatorGateway = validatorGateway;
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

//    private Mono<EmailValidationResult> validateDescription(String name, String messageId) {
//        return validatorGateway.validateName(name, messageId)
//                .filter(validationResult -> validationResult.deliverability().equals(Constants.DELIVERABLE))
//                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.INVALID_EMAIL)));
//    }

}

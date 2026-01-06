package com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper;

import com.example.resilient_api.domain.model.CapacityTechnology;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity.CapacityTechnologyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CapacityTechnologyEntityMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "idTechnology", target = "idTechnology")
    @Mapping(source = "idCapacity", target = "idCapacity")
    CapacityTechnology toModel(CapacityTechnologyEntity capacityTechnologyEntity);
    CapacityTechnologyEntity toEntity(CapacityTechnology capacityTechnology);

}

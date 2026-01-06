package com.example.resilient_api.infrastructure.entrypoints.mapper;

import com.example.resilient_api.domain.model.CapacityTechnology;
import com.example.resilient_api.infrastructure.entrypoints.dto.CapacityTechnologyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CapacityTechnologyMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "id_capacity", target = "idCapacity")
    @Mapping(source = "id_tecnology", target = "idTechnology")
    CapacityTechnology capacityTechnologyDTOToCapacityTechnology(CapacityTechnologyDTO capacityTechnologyDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(target = "id_capacity", source = "idCapacity")
    @Mapping(target = "id_tecnology", source = "idTechnology")
    CapacityTechnologyDTO capacityTechnologyToCapacityTechnologyDTO(CapacityTechnology capacityTechnology);

}

package com.example.resilient_api.infrastructure.entrypoints.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class CapacityTechnologyDTO {
    private Long id;
    @JsonProperty("id_capacity")
    private Long id_capacity;
    @JsonProperty("id_tecnology")
    private Long id_tecnology;

}
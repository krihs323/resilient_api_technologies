package com.example.resilient_api.infrastructure.entrypoints.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class TechnologyDTO {
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres.")
    private String name;

    @NotBlank(message = "La descripcion no puede estar vacía")
    @Size(min = 1, max = 50, message = "La descripcion debe tener entre 1 y 90 caracteres.")
    private String description;
}

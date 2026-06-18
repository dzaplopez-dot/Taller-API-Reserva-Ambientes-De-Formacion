package com.agendasena.agendasena.dto;

import com.agendasena.agendasena.model.TipoAmbiente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmbienteDTO {
    private Long id;
    private String nombre;
    private TipoAmbiente tipo;
    private Integer capacidad;
    private Boolean activo;
}

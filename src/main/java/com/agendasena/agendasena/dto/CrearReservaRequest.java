package com.agendasena.agendasena.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearReservaRequest {
    private Long ambienteId;
    private String instructorNombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer numeroAprendices;
}

package com.agendasena.agendasena.dto;

import com.agendasena.agendasena.model.EstadoReserva;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {
    private Long id;
    private Long ambienteId;
    private String nombreAmbiente;
    private String instructorNombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer numeroAprendices;
    private EstadoReserva estado;
}

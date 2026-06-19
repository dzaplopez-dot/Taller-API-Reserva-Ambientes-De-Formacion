package com.agendasena.agendasena.controller;

import com.agendasena.agendasena.dto.AmbienteDTO;
import com.agendasena.agendasena.dto.ReservaDTO;
import com.agendasena.agendasena.service.AmbienteService;
import com.agendasena.agendasena.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ambientes")
@RequiredArgsConstructor
public class AmbienteController {

    private final AmbienteService ambienteService;
    private final ReservaService reservaService;

    
    @PostMapping
    public ResponseEntity<AmbienteDTO> crearAmbiente(@RequestBody AmbienteDTO ambienteDTO) {
        AmbienteDTO nuevoAmbiente = ambienteService.crearAmbiente(ambienteDTO);
        return new ResponseEntity<>(nuevoAmbiente, HttpStatus.CREATED);
    }

    
    @GetMapping
    public ResponseEntity<List<AmbienteDTO>> listarAmbientes() {
        List<AmbienteDTO> ambientes = ambienteService.listarAmbientes();
        return ResponseEntity.ok(ambientes);
    }

    
    @GetMapping("/disponibles")
    public ResponseEntity<List<AmbienteDTO>> listarAmbientesDisponibles(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fin) {
        
        List<AmbienteDTO> disponibles = ambienteService.listarAmbientesDisponibles(inicio, fin);
        return ResponseEntity.ok(disponibles);
    }

    
    @GetMapping("/{id}/reservas")
    public ResponseEntity<List<ReservaDTO>> listarReservasPorAmbiente(
            @PathVariable Long id,
            @RequestParam LocalDateTime fecha) {
        
        List<ReservaDTO> reservas = reservaService.listarReservasActivasPorAmbienteYFecha(id, fecha);
        return ResponseEntity.ok(reservas);
    }
}
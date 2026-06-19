package com.agendasena.agendasena.controller;

import com.agendasena.agendasena.dto.CrearReservaRequest;
import com.agendasena.agendasena.dto.ReservaDTO;
import com.agendasena.agendasena.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    // =============================================
    // POST /api/reservas - Crear una reserva
    // =============================================
    @PostMapping
    public ResponseEntity<ReservaDTO> crearReserva(@RequestBody CrearReservaRequest request) {
        ReservaDTO nuevaReserva = reservaService.crearReserva(request);
        return new ResponseEntity<>(nuevaReserva, HttpStatus.CREATED);
    }

    // =============================================
    // PATCH /api/reservas/{id}/cancelar - Cancelar reserva
    // =============================================
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaDTO> cancelarReserva(@PathVariable Long id) {
        ReservaDTO reservaCancelada = reservaService.cancelarReserva(id);
        return ResponseEntity.ok(reservaCancelada);
    }
}
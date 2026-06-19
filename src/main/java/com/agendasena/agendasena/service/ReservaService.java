package com.agendasena.agendasena.service;

import com.agendasena.agendasena.dto.CrearReservaRequest;
import com.agendasena.agendasena.dto.ReservaDTO;
import com.agendasena.agendasena.model.Ambiente;
import com.agendasena.agendasena.model.EstadoReserva;
import com.agendasena.agendasena.model.Reserva;
import com.agendasena.agendasena.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final AmbienteService ambienteService;

    // Crear reserva aplicando las 7 reglas de negocio
    @Transactional
    public ReservaDTO crearReserva(CrearReservaRequest request) {

        // Validar que el ambiente existe
        Ambiente ambiente = ambienteService.obtenerPorId(request.getAmbienteId());

        // REGLA 4: Ambiente debe estar activo
        if (!ambiente.getActivo()) {
            throw new RuntimeException("El ambiente no está activo.");
        }

        // REGLA 2: Capacidad suficiente
        if (request.getNumeroAprendices() > ambiente.getCapacidad()) {
            throw new RuntimeException("Capacidad insuficiente.");
        }

        // REGLA 7: No reservar en pasado
        if (request.getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede reservar en pasado.");
        }

        // REGLA 3: Horario entre 6:00 y 22:00
        LocalTime inicio = request.getFechaInicio().toLocalTime();
        LocalTime fin = request.getFechaFin().toLocalTime();
        if (inicio.isBefore(LocalTime.of(6, 0)) || fin.isAfter(LocalTime.of(22, 0))) {
            throw new RuntimeException("Horario debe estar entre 6:00 y 22:00.");
        }

        // REGLA 3: Duración entre 1 y 4 horas
        long horas = Duration.between(request.getFechaInicio(), request.getFechaFin()).toHours();
        if (horas < 1 || horas > 4) {
            throw new RuntimeException("La reserva debe durar entre 1 y 4 horas.");
        }

        // REGLA 1: Sin cruces de horario
        List<Reserva> solapadas = reservaRepository.findReservasActivasSolapadas(
                ambiente.getId(),
                request.getFechaInicio(),
                request.getFechaFin());
        if (!solapadas.isEmpty()) {
            throw new RuntimeException("El ambiente ya está reservado en ese horario.");
        }

        // REGLA 5: Máximo 3 reservas activas por instructor en el día
        Long reservasHoy = reservaRepository.countReservasActivasPorInstructorYFecha(
                request.getInstructorNombre(),
                request.getFechaInicio());
        if (reservasHoy >= 3) {
            throw new RuntimeException("El instructor ya tiene 3 reservas hoy.");
        }

        // Crear y guardar la reserva
        Reserva reserva = new Reserva();
        reserva.setAmbiente(ambiente);
        reserva.setInstructorNombre(request.getInstructorNombre());
        reserva.setFechaInicio(request.getFechaInicio());
        reserva.setFechaFin(request.getFechaFin());
        reserva.setNumeroAprendices(request.getNumeroAprendices());
        reserva.setEstado(EstadoReserva.ACTIVA);

        return toDTO(reservaRepository.save(reserva));
    }

    // REGLA 6: Cancelar reserva con al menos 2 horas de anticipación
    @Transactional
    public ReservaDTO cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Solo se puede cancelar si está activa
        if (reserva.getEstado() != EstadoReserva.ACTIVA) {
            throw new RuntimeException("Solo se pueden cancelar reservas activas.");
        }

        // Validar anticipación mínima de 2 horas
        if (LocalDateTime.now().plusHours(2).isAfter(reserva.getFechaInicio())) {
            throw new RuntimeException("Debe haber al menos 2 horas de anticipación para cancelar.");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        return toDTO(reservaRepository.save(reserva));
    }

    // Listar reservas activas de un ambiente en una fecha específica
    public List<ReservaDTO> listarReservasActivasPorAmbienteYFecha(Long ambienteId, LocalDateTime fecha) {
        return reservaRepository.findReservasActivasPorAmbienteYFecha(ambienteId, fecha)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Convertir entidad Reserva a DTO
    private ReservaDTO toDTO(Reserva reserva) {
        return new ReservaDTO(
                reserva.getId(),
                reserva.getAmbiente().getId(),
                reserva.getAmbiente().getNombre(),
                reserva.getInstructorNombre(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getNumeroAprendices(),
                reserva.getEstado());
    }
}
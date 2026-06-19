package com.agendasena.agendasena.service;

import com.agendasena.agendasena.dto.AmbienteDTO;
import com.agendasena.agendasena.model.Ambiente;
import com.agendasena.agendasena.model.TipoAmbiente;
import com.agendasena.agendasena.repository.AmbienteRepository;
import com.agendasena.agendasena.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AmbienteService {

    private final AmbienteRepository ambienteRepository;
    private final ReservaRepository reservaRepository;

    public AmbienteDTO crearAmbiente(AmbienteDTO dto) {
        Ambiente ambiente = new Ambiente();
        ambiente.setNombre(dto.getNombre());
        ambiente.setTipo(dto.getTipo());
        ambiente.setCapacidad(dto.getCapacidad());
        ambiente.setActivo(true);
        return toDTO(ambienteRepository.save(ambiente));
    }

    public List<AmbienteDTO> listarAmbientes() {
        return ambienteRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public List<AmbienteDTO> listarActivos() {
        return ambienteRepository.findByActivoTrue().stream()
                .map(this::toDTO)
                .toList();
    }

    public List<AmbienteDTO> listarPorTipo(TipoAmbiente tipo) {
        return ambienteRepository.findByTipo(tipo).stream()
                .map(this::toDTO)
                .toList();
    }

    public Ambiente obtenerPorId(Long id) {
        return ambienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ambiente no encontrado con ID: " + id));
    }

    public List<AmbienteDTO> listarAmbientesDisponibles(LocalDateTime inicio, LocalDateTime fin) {
        return ambienteRepository.findByActivoTrue().stream()
                .filter(ambiente -> reservaRepository.findReservasActivasSolapadas(
                        ambiente.getId(), inicio, fin).isEmpty()) // ← ¡Paréntesis cerrado!
                .map(this::toDTO)
                .toList();
    }

    private AmbienteDTO toDTO(Ambiente ambiente) {
        return new AmbienteDTO(
                ambiente.getId(),
                ambiente.getNombre(),
                ambiente.getTipo(),
                ambiente.getCapacidad(),
                ambiente.getActivo());
    }
}
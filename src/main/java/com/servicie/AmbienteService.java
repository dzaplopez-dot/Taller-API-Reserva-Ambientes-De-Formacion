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

    public List<AmbienteDTO> listarAmbientesDisponibles(LocalDateTime inicio, LocalDateTime fin) {
        return ambienteRepository.findByActivoTrue().stream()
                .filter(ambiente -> reservaRepository.findReservasActivasSolapadas(
                        ambiente.getId(), inicio, fin).isEmpty())
                .map(this::toDTO)
                .toList();
    }
}
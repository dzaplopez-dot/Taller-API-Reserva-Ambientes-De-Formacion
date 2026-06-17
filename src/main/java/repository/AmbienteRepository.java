package com.agendasena.agendasena.repository;

import com.agendasena.agendasena.model.Ambiente;
import com.agendasena.agendasena.model.TipoAmbiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AmbienteRepository extends JpaRepository<Ambiente, Long> {

    List<Ambiente> findByActivoTrue();

    List<Ambiente> findByTipo(TipoAmbiente tipo);

    List<Ambiente> findByCapacidadGreaterThanEqual(Integer capacidad);

    List<Ambiente> findByActivoTrueAndCapacidadGreaterThanEqual(Integer capacidad);
}
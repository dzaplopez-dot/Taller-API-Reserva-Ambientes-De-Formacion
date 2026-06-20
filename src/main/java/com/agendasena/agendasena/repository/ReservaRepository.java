package com.agendasena.agendasena.repository;

import com.agendasena.agendasena.model.EstadoReserva;
import com.agendasena.agendasena.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

        @Query("SELECT r FROM Reserva r WHERE r.ambiente.id = :ambienteId " +
                        "AND r.estado = 'ACTIVA' " +
                        "AND r.fechaInicio < :fin " +
                        "AND r.fechaFin > :inicio")
        List<Reserva> findReservasActivasSolapadas(
                        @Param("ambienteId") Long ambienteId,
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);

        @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
                        "FROM Reserva r WHERE r.ambiente.id = :ambienteId " +
                        "AND r.estado = 'ACTIVA' " +
                        "AND r.fechaInicio < :fin " +
                        "AND r.fechaFin > :inicio")
        boolean existeReservaActivaEnRango(
                        @Param("ambienteId") Long ambienteId,
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);

        @Query("SELECT COUNT(r) FROM Reserva r WHERE r.instructorNombre = :instructor " +
                        "AND r.estado = 'ACTIVA' " +
                        "AND CAST(r.fechaInicio AS DATE) = CAST(:fecha AS DATE)")
        Long countReservasActivasPorInstructorYFecha(
                        @Param("instructor") String instructor,
                        @Param("fecha") LocalDateTime fecha);

        @Query("SELECT r FROM Reserva r WHERE r.ambiente.id = :ambienteId " +
                        "AND r.estado = 'ACTIVA' " +
                        "AND CAST(r.fechaInicio AS DATE) = CAST(:fecha AS DATE)")
        List<Reserva> findReservasActivasPorAmbienteYFecha(
                        @Param("ambienteId") Long ambienteId,
                        @Param("fecha") LocalDateTime fecha);

        List<Reserva> findByAmbienteId(Long ambienteId);

        List<Reserva> findByInstructorNombreAndEstado(String instructor, EstadoReserva estado);

        List<Reserva> findByEstado(EstadoReserva estado);

        List<Reserva> findByEstadoAndFechaInicioAfter(EstadoReserva estado, LocalDateTime fecha);

        List<Reserva> findByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);
}
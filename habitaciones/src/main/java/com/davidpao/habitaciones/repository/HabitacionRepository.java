package com.davidpao.habitaciones.repository;

import com.davidpao.commons.enums.EstadoRegistro;
import com.davidpao.habitaciones.entity.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    List<Habitacion> findByEstadoRegistro(EstadoRegistro estadoRegistro);

    Optional<Habitacion> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);

    boolean existsByNumeroHabitacionAndEstadoRegistro(Integer numeroHabitacion, EstadoRegistro estadoRegistro);

    Optional<Habitacion> findByNumeroHabitacionAndEstadoRegistro(Integer numeroHabitacion, EstadoRegistro estadoRegistro);
}

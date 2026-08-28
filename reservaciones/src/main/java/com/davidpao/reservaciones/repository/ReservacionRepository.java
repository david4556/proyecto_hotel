package com.davidpao.reservaciones.repository;

import com.davidpao.reservaciones.entity.Reservacion;
import com.davidpao.reservaciones.enums.EstadoReservacion;
import com.davidpao.commons.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservacionRepository
        extends JpaRepository<Reservacion, Long> {

    List<Reservacion> findByEstadoRegistro(
            EstadoRegistro estadoRegistro
    );

    Optional<Reservacion> findByIdAndEstadoRegistro(
            Long id,
            EstadoRegistro estadoRegistro
    );

    boolean existsByIdHuespedAndEstadoReservaInAndIdNot(
            Long idHuesped,
            Collection<EstadoReservacion> estados,
            Long id
    );

    boolean existsByIdHabitacionAndEstadoReservaInAndIdNot(
            Long idHabitacion,
            Collection<EstadoReservacion> estados,
            Long id
    );

    boolean existsByIdHuespedAndEstadoReservaIn(
            Long idHuesped,
            List<EstadoReservacion> estados
    );

    boolean existsByIdHabitacionAndEstadoReservaIn(
            Long idHabitacion,
            List<EstadoReservacion> estados
    );
}
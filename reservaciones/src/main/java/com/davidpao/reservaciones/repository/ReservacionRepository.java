package com.davidpao.reservaciones.repository;

import com.davidpao.reservaciones.entity.Reservacion;
import com.davidpao.reservaciones.enums.EstadoReservacion;
import com.davidpao.commons.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservacionRepository
        extends JpaRepository<Reservacion, Long> {

    List<Reservacion> findByEstadoRegistro(
            EstadoRegistro estadoRegistro
    );

    boolean existsByIdHuespedAndEstadoReserva(Long idHuesped, EstadoReservacion estadoReservacion);

}
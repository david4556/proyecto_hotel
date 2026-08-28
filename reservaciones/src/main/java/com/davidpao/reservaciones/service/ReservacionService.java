package com.davidpao.reservaciones.service;

import com.davidpao.reservaciones.dto.ReservacionRequest;
import com.davidpao.reservaciones.dto.ReservacionResponse;
import com.davidpao.commons.service.CrudService;

public interface ReservacionService
        extends CrudService<ReservacionRequest, ReservacionResponse> {

    void actualizarEstadoReservacion(Long idReservacion, Long idEstadoReservacion
    );

    boolean tieneReservacionConfirmadaOEnCursoHabitacion(Long idHabitacion
    );

}
package com.davidpao.reservaciones.mapper;

import com.davidpao.commons.dto.habitacion.DatosHabitacion;
import com.davidpao.commons.dto.habitacion.HabitacionResponse;
import com.davidpao.commons.dto.huespedes.DatosHuesped;
import com.davidpao.commons.dto.huespedes.HuespedResponse;
import com.davidpao.commons.mapper.CommonMapper;
import com.davidpao.reservaciones.dto.ReservacionRequest;
import com.davidpao.reservaciones.dto.ReservacionResponse;
import com.davidpao.reservaciones.entity.Reservacion;
import org.springframework.stereotype.Component;

@Component
public class ReservacionMapper implements CommonMapper<ReservacionRequest, ReservacionResponse, Reservacion> {

    @Override
    public Reservacion requestAEntidad(ReservacionRequest request) {

        if (request == null) return null;

        return Reservacion.crear(
                request.idHuesped(),
                request.idHabitacion(),
                request.fechaEntrada(),
                request.fechaSalida()
        );
    }

    @Override
    public ReservacionResponse entidadAResponse(Reservacion entidad) {

        if (entidad == null) return null;

        return new ReservacionResponse(
                entidad.getId(),
                null,
                null,
                entidad.getFechaEntrada(),
                entidad.getFechaSalida(),
                entidad.getEstadoReserva().getDescripcion()
        );
    }

    public ReservacionResponse entidadAResponse(
            Reservacion entidad,
            HuespedResponse huesped,
            HabitacionResponse habitacion) {

        if (entidad == null) return null;

        return new ReservacionResponse(
                entidad.getId(),
                huespedResponseADatosHuesped(huesped),
                habitacionResponseADatosHabitacion(habitacion),
                entidad.getFechaEntrada(),
                entidad.getFechaSalida(),
                entidad.getEstadoReserva().getDescripcion()
        );
    }

    private DatosHuesped huespedResponseADatosHuesped(
            HuespedResponse huesped) {

        if (huesped == null) return null;

        return new DatosHuesped(
                huesped.id(),
                huesped.nombre()
        );
    }

    private DatosHabitacion habitacionResponseADatosHabitacion(
            HabitacionResponse habitacion) {

        if (habitacion == null) return null;

        return new DatosHabitacion(
                habitacion.id(),
                habitacion.numeroHabitacion(),
                habitacion.tipoHabitacion()
        );
    }
}
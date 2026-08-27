package com.davidpao.habitaciones.mappers;

import com.davidpao.commons.dto.habitacion.HabitacionRequest;
import com.davidpao.commons.dto.habitacion.HabitacionResponse;
import com.davidpao.commons.enums.EstadoHabitacion;
import com.davidpao.commons.enums.EstadoRegistro;
import com.davidpao.commons.enums.TipoHabitacion;
import com.davidpao.commons.mapper.CommonMapper;
import com.davidpao.habitaciones.entity.Habitacion;
import org.springframework.stereotype.Component;

@Component
public class HabitacionMapper implements CommonMapper<HabitacionRequest, HabitacionResponse, Habitacion> {
    @Override

    public Habitacion requestAEntidad( HabitacionRequest request) {
        if (request == null) return null;

        return Habitacion.builder()
                .numeroHabitacion(request.numeroHabitacion())
                .tipoHabitacion(TipoHabitacion.obtenerTipoHabitacionPorCodigo(request.idTipoHabitacion()))
                .capacidad(request.capacidad())
                .estadoHabitacion(EstadoHabitacion.DISPONIBLE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }

    public HabitacionResponse entidadAResponse(Habitacion entidad) {
        if (entidad == null) return null;

        return new HabitacionResponse(

                entidad.getId(),
                entidad.getNumeroHabitacion(),
                entidad.getTipoHabitacion().getDescripcion(),
                entidad.getPrecio(),
                entidad.getCapacidad(),
                entidad.getEstadoHabitacion().getDescripcion()

        );


    }


}

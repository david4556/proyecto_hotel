package com.davidpao.commons.enums;

import com.davidpao.commons.exceptions.RecursoNoEncontradoException;
import com.davidpao.commons.utils.StringCustomUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum EstadoHabitacion {
    DISPONIBLE (1L,"Disponible"),
    OCUPADA (2L,"Ocupada"),
    MANTENIMIENTO(3L,"Mantenimiento"),
    RESERVADA (4L,"Reservada");

    private final Long codigo;
    private final String descripcion;

    public static EstadoHabitacion obtenerEstadoHabitacionPorDescripcion(String descripcion){
        StringCustomUtils.validarNoVacio(descripcion, "La descripción es requerida");
        String descripcionNorm = StringCustomUtils.quitarAcentos(descripcion);
        for (EstadoHabitacion estado: values()){
            if (StringCustomUtils.quitarAcentos(estado.descripcion.toLowerCase()).equalsIgnoreCase(descripcionNorm.toLowerCase()))
                return estado;
        }
        throw new RecursoNoEncontradoException("No existe categoria con la descripción:" + descripcion);
    }

    public static EstadoHabitacion obtenerEstadoHabitacionPorCodigo(Long codigo){
        if (codigo == null || codigo < 0)
            throw new IllegalArgumentException("Codigo debe ser positivo o 0");

        for (EstadoHabitacion EstadoHabitacion :values()){
            if (Objects.equals(EstadoHabitacion.codigo, codigo))
                return EstadoHabitacion;
        }
        throw new RecursoNoEncontradoException("No existe estado de Venta con el codigo "+ codigo);
    }
}


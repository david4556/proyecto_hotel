package com.davidpao.commons.enums;

import com.davidpao.commons.exceptions.RecursoNoEncontradoException;
import com.davidpao.commons.utils.StringCustomUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum TipoHabitacion {
    SENCILLA (1L,"Sencilla"),
    SUITE (2L,"Suite"),
    FAMILIAR(3L,"Familiar"),
    EJECUTIVA (4L,"Ejecutiva");

    private final Long codigo;
    private final String descripcion;

    public static TipoHabitacion obtenerTipoHabitacionPorDescripcion(String descripcion){
        StringCustomUtils.validarNoVacio(descripcion, "La descripción es requerida");
        String descripcionNorm = StringCustomUtils.quitarAcentos(descripcion);
        for (TipoHabitacion tipo: values()){
            if (StringCustomUtils.quitarAcentos(tipo.descripcion.toLowerCase()).equalsIgnoreCase(descripcionNorm.toLowerCase()))
                return tipo;
        }
        throw new RecursoNoEncontradoException("No existe categoria con la descripción:" + descripcion);
    }

    public static TipoHabitacion obtenerTipoHabitacionPorCodigo(Long codigo){
        if (codigo == null || codigo < 0)
            throw new IllegalArgumentException("Codigo debe ser positivo o 0");

        for (TipoHabitacion TipoHabitacion :values()){
            if (Objects.equals(TipoHabitacion.codigo, codigo))
                return TipoHabitacion;
        }
        throw new RecursoNoEncontradoException("No existe tipo de Venta con el codigo "+ codigo);
    }

    public static TipoHabitacion obtenerDisponibilidadPorCodigo(Long codigo){

        for (TipoHabitacion em: values()){
            if (Objects.equals(em.codigo,codigo))
                return em;
        }

        throw new RecursoNoEncontradoException("Codigo de disponibilidad no valido: "+ codigo);
    }
}


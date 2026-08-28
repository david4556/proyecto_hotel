package com.davidpao.reservaciones.enums;

import com.davidpao.commons.exceptions.RecursoNoEncontradoException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
@Getter
@RequiredArgsConstructor
public enum EstadoReservacion {

    PENDIENTE(1L , "Pendiente de confirmar",true, true) {
        @Override
        public Set<EstadoReservacion> puedeCambiar() {
            return EnumSet.of(CONFIRMADA, CANCELADA);
        }
    },

    CONFIRMADA(2L , "Confirmada por el paciente", true, false) {
        @Override
        public Set<EstadoReservacion> puedeCambiar() {
            return EnumSet.of( EN_CURSO ,  CANCELADA);
        }
    },

    EN_CURSO(3L , "Pendiente llego a su cita ",true, false) {
        @Override
        public Set<EstadoReservacion> puedeCambiar() {
            return EnumSet.of(FINALIZADA);
        }
    },

    FINALIZADA(4L , "Cita finalizada" ,false, true ) {
        @Override
        public Set<EstadoReservacion> puedeCambiar() {
            return Set.of();
        }
    },

    CANCELADA(5L , "Cita Cancelada", false, true) {
        @Override
        public Set<EstadoReservacion> puedeCambiar() {
            return Set.of();
        }
    };

    private final Long codigo;

    private final String descripcion;

    private final boolean actualizable;

    private final boolean eliminable;

    public abstract Set<EstadoReservacion>puedeCambiar();



    public boolean puedeCambiarA(EstadoReservacion nuevoEstado){
        return puedeCambiar().contains(nuevoEstado);
    }


    public static EstadoReservacion obtenerEstadoCitaPorCodigo(Long codigo){

        for (EstadoReservacion e : values()){
            if (Objects.equals(e.codigo, codigo))
            {
                return e;
            }
        }
            throw new RecursoNoEncontradoException("codigo de cita no vlido" +codigo);


    }
}

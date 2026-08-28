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

    CONFIRMADA(1L , "Reserva creada", true, false) {
        @Override
        public Set<EstadoReservacion> puedeCambiar() {
            return EnumSet.of( EN_CURSO ,  CANCELADA);
        }
    },

    EN_CURSO(2L , "Check-in realizado",true, false) {
        @Override
        public Set<EstadoReservacion> puedeCambiar() {
            return EnumSet.of(FINALIZADA);
        }
    },

    FINALIZADA(3L , "Check-out realizado" ,false, true ) {
        @Override
        public Set<EstadoReservacion> puedeCambiar() {
            return Set.of();
        }
    },

    CANCELADA(4L , "Reserva cancelada", false, true) {
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

    public static EstadoReservacion obtenerEstadoReservaPorCodigo(Long codigo){

        for (EstadoReservacion e : values()){
            if (Objects.equals(e.codigo, codigo))
            {
                return e;
            }
        }
            throw new RecursoNoEncontradoException("codigo de reservacion no valido " +codigo);

    }
}

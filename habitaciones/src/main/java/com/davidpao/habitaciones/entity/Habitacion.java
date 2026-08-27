package com.davidpao.habitaciones.entity;

import com.davidpao.commons.enums.EstadoRegistro;
import com.davidpao.commons.enums.EstadoHabitacion;
import com.davidpao.commons.enums.TipoHabitacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "HABITACIONES")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter

public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HABITACION")
    private Long id;

    @Column(name = "NUMERO_HABITACION",nullable = false)
    private Integer numeroHabitacion;

    @Enumerated (EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "TIPO_HABITACION", nullable = false)
    private TipoHabitacion tipoHabitacion;

    @Column(name = "PRECIO", nullable = false)
    private BigDecimal precio;

    @Column(name = "CAPACIDAD", nullable = false)
    private Integer capacidad;

    @Enumerated (EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "ESTADO_HABITACION",nullable = false)
    private EstadoHabitacion estadoHabitacion;

    @Enumerated (EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    private void validarDatos(TipoHabitacion tipoHabitacion) {
        if (tipoHabitacion == null)
            throw new IllegalArgumentException("El tipo de habitacion es requerido");
    }

    public void validarNoEliminado(){

        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new IllegalArgumentException("La habitacion ya esta eliminada");
    }

    public void eliminar(){

        validarNoEliminado();

        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }

    public void actualizarTipoHabitacion(TipoHabitacion tipoHabitacion){

        validarNoEliminado();

        validarDatos(tipoHabitacion);

        this.tipoHabitacion= tipoHabitacion;
    }

    public void actualizarDisponibilidadHabitacion(EstadoHabitacion estadoHabitacion){
        validarNoEliminado();
        if (this.estadoHabitacion == null)
            throw new IllegalArgumentException("La disponibilidad de la habitacion es requerida");

        this.estadoHabitacion= estadoHabitacion;
    }

    public void actualizar (Integer numeroHabitacion, TipoHabitacion tipoHabitacion,
                            BigDecimal precio, Integer capacidad,
                            EstadoHabitacion estadoHabitacion, EstadoRegistro estadoRegistro){

        validarNoEliminado();

        validarDatos(tipoHabitacion);

        actualizarTipoHabitacion(tipoHabitacion);

        this.numeroHabitacion = numeroHabitacion;
        this.tipoHabitacion = tipoHabitacion;
        this.precio = precio;
        this.capacidad = capacidad;
        this.estadoHabitacion = estadoHabitacion;
        this.estadoRegistro = estadoRegistro;

    }
}

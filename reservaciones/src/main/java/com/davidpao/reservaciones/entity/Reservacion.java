package com.davidpao.reservaciones.entity;

import com.davidpao.reservaciones.enums.EstadoReservacion;
import com.davidpao.commons.enums.EstadoRegistro;
import com.davidpao.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Setter
@Entity
@Table(name = "RESERVACIONES")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Reservacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RESERVACION")
    private Long id;

    @Column(name = "ID_HUESPED", nullable = false)
    private Long idHuesped;

    @Column(name = "ID_HABITACION", nullable = false)
    private Long idHabitacion;

    @Column(name = "FECHA_ENTRADA", nullable = false)
    private LocalDate fechaEntrada;

    @Column(name = "FECHA_SALIDA", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "ESTADO_RESERVACION", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoReservacion estadoReserva;

    @Column(name = "ESTADO_REGISTRO", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoRegistro estadoRegistro;


    public static void validarId(Long id, String campo) {

        ValoresNumericosUtils.validarLongPositivo(
                id,
                "el id de " + campo + " es requerido y debe ser positivo"
        );
    }


    private static void validarFechas(
            LocalDate fechaEntrada,
            LocalDate fechaSalida) {

        if (fechaEntrada == null || fechaSalida == null) {
            throw new IllegalArgumentException(
                    "las fechas de entrada y salida son requeridas"
            );
        }

        if (!fechaEntrada.isBefore(fechaSalida)) {
            throw new IllegalArgumentException(
                    "la fecha de entrada debe ser menor que la fecha de salida"
            );
        }
    }


    public static void validarDatos(
            Long idHuesped,
            Long idHabitacion,
            LocalDate fechaEntrada,
            LocalDate fechaSalida) {

        validarId(idHuesped, "huesped");

        validarId(idHabitacion, "habitacion");

        validarFechas(
                fechaEntrada,
                fechaSalida
        );
    }


    private void validarNoEliminada() {

        if (this.estadoRegistro == EstadoRegistro.ELIMINADO) {

            throw new IllegalStateException(
                    "la reservacion ya esta eliminada"
            );
        }
    }


    private void validarEliminacionPermitida() {

        validarNoEliminada();

        if (!estadoReserva.isEliminable()) {

            throw new IllegalStateException(
                    "la reservacion con estado "
                            + estadoReserva
                            + " no puede eliminarse"
            );
        }
    }


    private void validarActualizacionPermitida() {

        validarNoEliminada();

        if (!estadoReserva.isActualizable()) {

            throw new IllegalStateException(
                    "la reservacion con estado "
                            + estadoReserva
                            + " no puede actualizarse"
            );
        }
    }


    public void eliminar() {

        validarEliminacionPermitida();

        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }


    public void actualizar(
            Long idHuesped,
            Long idHabitacion,
            LocalDate fechaEntrada,
            LocalDate fechaSalida) {

        validarActualizacionPermitida();

        validarDatos(
                idHuesped,
                idHabitacion,
                fechaEntrada,
                fechaSalida
        );

        this.idHuesped = idHuesped;
        this.idHabitacion = idHabitacion;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
    }


    public void actualizarEstadoReservacion(EstadoReservacion nuevoEstado) {
        validarNoEliminada();
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado de la reservación no puede ser nulo.");
        }
        this.estadoReserva = nuevoEstado;
    }


    public static Reservacion crear(
            Long idHuesped,
            Long idHabitacion,
            LocalDate fechaEntrada,
            LocalDate fechaSalida) {

        validarDatos(
                idHuesped,
                idHabitacion,
                fechaEntrada,
                fechaSalida
        );

        return Reservacion.builder()
                .idHuesped(idHuesped)
                .idHabitacion(idHabitacion)
                .fechaEntrada(fechaEntrada)
                .fechaSalida(fechaSalida)
                .estadoReserva(EstadoReservacion.CONFIRMADA)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }
}
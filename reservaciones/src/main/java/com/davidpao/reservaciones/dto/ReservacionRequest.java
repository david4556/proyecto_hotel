package com.davidpao.reservaciones.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


import java.time.LocalDate;

public record ReservacionRequest(

        @NotNull(message = "el id del huesped es requerido")
        @Positive(message = "el id del huesped debe ser positivo")
        Long idHuesped,

        @NotNull(message = "el id de la habitacion es requerido")
        @Positive(message = "el id de la habitacion debe ser positivo")
        Long idHabitacion,

        @NotNull(message = "la fecha de entrada es requerida")
        @FutureOrPresent(message = "la fecha de entrada debe ser presente o futura")
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "dd/MM/yyyy"
        )
        LocalDate fechaEntrada,

        @NotNull(message = "la fecha de salida es requerida")
        @FutureOrPresent(message = "la fecha de salida debe ser presente o futura")
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "dd/MM/yyyy"
        )
        LocalDate fechaSalida

) {
}
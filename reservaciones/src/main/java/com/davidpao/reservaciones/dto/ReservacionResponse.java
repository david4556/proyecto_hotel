package com.davidpao.reservaciones.dto;

import com.davidpao.commons.dto.datos.DatosHabitacion;
import com.davidpao.commons.dto.datos.DatosHuesped;
import com.fasterxml.jackson.annotation.JsonFormat;


import java.time.LocalDate;

public record ReservacionResponse(
        Long id,
        DatosHuesped huesped,
        DatosHabitacion habitacion,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate fechaEntrada,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate fechaSalida,

        String estadoReserva
) {
}
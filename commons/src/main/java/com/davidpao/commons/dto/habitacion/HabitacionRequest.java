package com.davidpao.commons.dto.habitacion;

import com.davidpao.commons.enums.TipoHabitacion;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record HabitacionRequest(
        @NotNull(message = "El numeroHabitacion de la habitacion es obligatorio")
        @Positive(message = "El numeroHabitacion de la habitacion debe ser positivo")
        Integer numeroHabitacion,

        @NotNull(message = "El tipo de habitacion es obligatorio")
        @Positive(message = "El tipo de habitacion debe ser positivo")
        Long idTipoHabitacion,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.01", inclusive = true, message = "El precio debe ser mayor a 0")
        BigDecimal precio,

        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad mínima es de 1 persona")
        Integer capacidad

) {
}

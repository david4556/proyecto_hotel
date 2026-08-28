package com.davidpao.commons.dto.datos;

import java.math.BigDecimal;

public record DatosHabitacion(
        Integer numeroHabitacion,
        String tipo,
        BigDecimal precio,
        Integer capacidad
) {
}

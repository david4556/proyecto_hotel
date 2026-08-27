package com.davidpao.commons.dto.habitacion;

import java.math.BigDecimal;

public record HabitacionResponse (
        Long id,
        Integer numeroHabitacion,
        String tipo,
        BigDecimal precio,
        Integer capacidad,
        String estadoHabitacion){
}

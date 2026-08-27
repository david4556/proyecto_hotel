package com.davidpao.habitaciones.services;

import com.davidpao.commons.dto.habitacion.HabitacionRequest;
import com.davidpao.commons.dto.habitacion.HabitacionResponse;
import com.davidpao.commons.dto.huespedes.HuespedResponse;
import com.davidpao.commons.service.CrudService;

public interface HabitacionService extends CrudService<HabitacionRequest, HabitacionResponse> {
    HabitacionResponse obtenerHabitacionPorIdSinEstado(Long id);
}

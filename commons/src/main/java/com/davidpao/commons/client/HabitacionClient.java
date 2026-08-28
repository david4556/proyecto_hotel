package com.davidpao.commons.client;

import com.davidpao.commons.dto.habitacion.HabitacionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "habitaciones")
public interface HabitacionClient {

    @GetMapping("/{id}")
    HabitacionResponse obtenerHabitacionActivoPorId(@PathVariable Long id);

    @GetMapping("/id-habitacion/{id}")
    HabitacionResponse obtenerHabitacionPorIdSinEstado(@PathVariable Long id);

    @PutMapping("/{idHabitacion}/disponibilidad-habitacion/{idDisponibilidad}")
    void actualizarDisponibilidadHabitacion(
            @PathVariable("idHabitacion") Long idHabitacion,
            @PathVariable("idDisponibilidad") Long idDisponibilidad
    );


}

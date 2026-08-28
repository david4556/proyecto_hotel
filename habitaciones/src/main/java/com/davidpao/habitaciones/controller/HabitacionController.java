package com.davidpao.habitaciones.controller;

import com.davidpao.commons.controller.CommonController;
import com.davidpao.commons.dto.habitacion.HabitacionRequest;
import com.davidpao.commons.dto.habitacion.HabitacionResponse;
import com.davidpao.habitaciones.services.HabitacionService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
public class HabitacionController
        extends CommonController<HabitacionRequest, HabitacionResponse, HabitacionService> {

    public HabitacionController(HabitacionService service) {
        super(service);
    }

    @GetMapping("/id-Habitacion/{id}")
    public ResponseEntity<HabitacionResponse> obtenerHabitacionPorIdSinEstado(
            @PathVariable
            @Positive(message = "El id debe ser positivo")
            Long id
    ) {
        return ResponseEntity.ok(service.obtenerHabitacionPorIdSinEstado(id));
    }

    @PatchMapping("/{idHabitacion}/estado/{idEstadoHabitacion}")
    public ResponseEntity<Void> actualizarEstadoHabitacion(
            @PathVariable @Positive (message = "El idHabitacion debe ser positivo") Long idHabitacion,
            @PathVariable @Positive (message = "El idEstado debe ser positivo") Long idEstadoHabitacion){
        service.actualizarEstadoHabitacion(idHabitacion,idEstadoHabitacion);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{idHabitacion}/disponibilidad-habitacion/{idDisponibilidad}")
    public ResponseEntity<Void> actualizarDisponibilidadHabitacion(
            @PathVariable("idHabitacion") Long idHabitacion,
            @PathVariable("idDisponibilidad") Long idDisponibilidad) {

        service.actualizarEstadoHabitacion(idHabitacion, idDisponibilidad);
        return ResponseEntity.noContent().build();
    }
}
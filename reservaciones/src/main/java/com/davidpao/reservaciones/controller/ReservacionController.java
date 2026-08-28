package com.davidpao.reservaciones.controller;

import com.davidpao.commons.controller.CommonController;
import com.davidpao.reservaciones.dto.ReservacionRequest;
import com.davidpao.reservaciones.dto.ReservacionResponse;
import com.davidpao.reservaciones.service.ReservacionService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
public class ReservacionController extends CommonController<ReservacionRequest, ReservacionResponse, ReservacionService> {
    public ReservacionController(ReservacionService service) {
        super(service);
    }



    @PatchMapping("/{idReservacion}/estado/{idEstado}")
    public ResponseEntity<Void> actualizarEstadoReservacion(
            @PathVariable @Positive(message = "El idReservacion debe ser positivo") Long idReservacion,
            @PathVariable @Positive (message = "El idEstado debe ser positivo") Long idEstado){
        service.actualizarEstadoReservacion(idReservacion,idEstado);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/huespedes/{idHuesped}/tiene-reservas-en-curso")
    public ResponseEntity<Boolean> tieneReservasEnCurso(@PathVariable("idHuesped") Long idHuesped) {
        boolean tieneReservas = service.tieneReservasEnCurso(idHuesped);
        return ResponseEntity.ok(tieneReservas);
    }

}

package com.davidpao.commons.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "reservaciones")

public interface ReservacionClient {

    @GetMapping("/huespedes/{idHuesped}/tiene-reservas-en-curso")
    Boolean tieneReservasEnCurso(@PathVariable("idHuesped") Long idHuesped);
}

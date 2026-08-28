package com.davidpao.commons.client;

import com.davidpao.commons.dto.huespedes.HuespedResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "huespedes")
public interface HuespedClient {

    @GetMapping("/{id}")
    HuespedResponse obtenerHuespedActivoPorId(
            @PathVariable("id") Long id
    );

    @GetMapping("/id-huesped/{id}")
    HuespedResponse obtenerHuespedPorIdSinEstado(
            @PathVariable("id") Long id
    );


}
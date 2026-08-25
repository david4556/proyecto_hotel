package com.davidpao.huespedes.controller;

import com.davidpao.commons.controller.CommonController;
import com.davidpao.commons.dto.huespedes.HuespedRequest;
import com.davidpao.commons.dto.huespedes.HuespedResponse;
import com.davidpao.huespedes.service.HuespedService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
public class HuespedController
        extends CommonController<HuespedRequest, HuespedResponse, HuespedService> {

    public HuespedController(HuespedService service) {
        super(service);
    }

    @GetMapping("/id-huesped/{id}")
    public ResponseEntity<HuespedResponse> obtenerHuespedPorIdSinEstado(
            @PathVariable
            @Positive(message = "El id debe ser positivo")
            Long id
    ) {
        return ResponseEntity.ok(service.obtenerHuespedPorIdSinEstado(id));
    }
}
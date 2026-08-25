package com.davidpao.huespedes.service;

import com.davidpao.commons.dto.huespedes.HuespedRequest;
import com.davidpao.commons.dto.huespedes.HuespedResponse;
import com.davidpao.commons.service.CrudService;

public interface HuespedService extends CrudService<HuespedRequest, HuespedResponse> {

    HuespedResponse obtenerHuespedPorIdSinEstado(Long id);
}
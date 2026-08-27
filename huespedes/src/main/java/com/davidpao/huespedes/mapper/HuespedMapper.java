package com.davidpao.huespedes.mapper;

import com.davidpao.commons.dto.huespedes.HuespedRequest;
import com.davidpao.commons.dto.huespedes.HuespedResponse;
import com.davidpao.commons.enums.EstadoRegistro;
import com.davidpao.commons.mapper.CommonMapper;
import com.davidpao.huespedes.entity.Huesped;
import org.springframework.stereotype.Component;

@Component
public class HuespedMapper
        implements CommonMapper<HuespedRequest, HuespedResponse, Huesped> {

    @Override
    public Huesped requestAEntidad(HuespedRequest request) {

        if (request == null) return null;

        return Huesped.builder()
                .nombre(request.nombre().trim())
                .apellidoPaterno(request.apellidoPaterno().trim())
                .apellidoMaterno(request.apellidoMaterno().trim())
                .email(request.email().toLowerCase().trim())
                .telefono(request.telefono().trim())
                .documento(request.documento().trim())
                .nacionalidad(request.nacionalidad().trim())
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }

    @Override
    public HuespedResponse entidadAResponse(Huesped entidad) {

        if (entidad == null) return null;

        return new HuespedResponse(
                entidad.getId(),
                String.join(" ",
                        entidad.getNombre(),
                        entidad.getApellidoPaterno(),
                        entidad.getApellidoMaterno()),
                entidad.getEmail(),
                entidad.getTelefono(),
                entidad.getDocumento(),
                entidad.getNacionalidad(),
                entidad.getEstadoRegistro()
        );
    }
}
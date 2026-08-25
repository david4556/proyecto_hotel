package com.davidpao.commons.dto.huespedes;

import com.davidpao.commons.enums.EstadoRegistro;

public record HuespedResponse(
        Long idHuesped,
        String nombreCompleto,
        String email,
        String telefono,
        String documento,
        String nacionalidad,
        EstadoRegistro estadoRegistro
) {
}
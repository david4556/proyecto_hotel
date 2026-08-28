package com.davidpao.commons.dto.datos;

public record DatosHuesped(
        String nombreCompleto,
        String email,
        String telefono,
        String documento,
        String nacionalidad
) {
}
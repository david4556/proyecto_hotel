package com.davidpao.commons.dto;

public record CustomErrorResponse(
        int codigo,
        String mensaje
) {
}

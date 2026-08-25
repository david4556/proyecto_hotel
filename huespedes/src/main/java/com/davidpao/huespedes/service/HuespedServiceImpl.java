package com.davidpao.huespedes.service;

//import com.davidpao.commons.client.ReservaClient;
import com.davidpao.commons.dto.huespedes.HuespedRequest;
import com.davidpao.commons.dto.huespedes.HuespedResponse;
import com.davidpao.commons.enums.EstadoRegistro;
import com.davidpao.commons.exceptions.RecursoNoEncontradoException;
import com.davidpao.huespedes.entity.Huesped;
import com.davidpao.huespedes.mapper.HuespedMapper;
import com.davidpao.huespedes.repository.HuespedRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Slf4j
@Service
@AllArgsConstructor
public class HuespedServiceImpl implements HuespedService {

    private final HuespedRepository huespedRepository;

    private final HuespedMapper huespedMapper;

    //private final ReservaClient reservaClient;


    @Override
    @Transactional(readOnly = true)
    public List<HuespedResponse> listar() {

        log.info("Listando huéspedes activos");

        return huespedRepository
                .findByEstadoRegistro(EstadoRegistro.ACTIVO)
                .stream()
                .map(huespedMapper::entidadAResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public HuespedResponse obtenerHuespedPorIdSinEstado(Long id) {

        log.info("Buscando huésped sin validar estado: {}", id);

        return huespedMapper.entidadAResponse(
                huespedRepository.findById(id)
                        .orElseThrow(() ->
                                new RecursoNoEncontradoException(
                                        "Huésped no encontrado: " + id
                                )
                        )
        );
    }


    @Override
    @Transactional(readOnly = true)
    public HuespedResponse obtenerPorId(Long id) {

        log.info("Buscando huésped activo: {}", id);

        return huespedMapper.entidadAResponse(
                obtenerHuespedActivoOException(id)
        );
    }


    @Override
    public HuespedResponse registrar(HuespedRequest request) {

        log.info("Registrando nuevo huésped: {}", request.nombre());

        validarDatosUnicos(request);

        Huesped huesped = huespedMapper.requestAEntidad(request);

        huesped.setEstadoRegistro(EstadoRegistro.ACTIVO);

        huespedRepository.save(huesped);

        log.info("Huésped registrado correctamente: {}", huesped.getId());

        return huespedMapper.entidadAResponse(huesped);
    }


    @Override
    public HuespedResponse actualizar(HuespedRequest request, Long id) {

        Huesped huesped = obtenerHuespedActivoOException(id);

        log.info("Actualizando huésped: {}", id);

        validarCambiosUnicos(request, id);

        huesped.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.email(),
                request.telefono(),
                request.documento(),
                request.nacionalidad()
        );

        log.info("Huésped actualizado correctamente: {}", id);

        return huespedMapper.entidadAResponse(huesped);
    }


    @Override
    public void eliminar(Long id) {

        Huesped huesped = obtenerHuespedActivoOException(id);

        //validarReservasActivas(id, "eliminar");

        log.info("Eliminando huésped: {}", id);

        huesped.eliminar();

        log.info("Huésped eliminado correctamente: {}", id);
    }


    private Huesped obtenerHuespedActivoOException(Long id) {

        log.info("Buscando huésped activo: {}", id);

        return huespedRepository
                .findByIdAndEstadoRegistro(
                        id,
                        EstadoRegistro.ACTIVO
                )
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Huésped no encontrado: " + id
                        )
                );
    }


    private void validarDatosUnicos(HuespedRequest request) {

        log.info("Validando datos únicos del huésped");


        if (huespedRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(),
                EstadoRegistro.ACTIVO
        )) {

            throw new IllegalArgumentException(
                    "Ya existe un huésped activo con el email: "
                            + request.email()
            );
        }


        if (huespedRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(),
                EstadoRegistro.ACTIVO
        )) {

            throw new IllegalArgumentException(
                    "Ya existe un huésped activo con el teléfono: "
                            + request.telefono()
            );
        }


        if (huespedRepository.existsByDocumentoIgnoreCaseAndEstadoRegistro(
                request.documento().trim(),
                EstadoRegistro.ACTIVO
        )) {

            throw new IllegalArgumentException(
                    "Ya existe un huésped activo con el documento: "
                            + request.documento()
            );
        }
    }


    private void validarCambiosUnicos(HuespedRequest request, Long id) {

        log.info("Validando cambios únicos del huésped: {}", id);


        if (huespedRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email().trim(),
                EstadoRegistro.ACTIVO,
                id
        )) {

            throw new IllegalArgumentException(
                    "Ya existe otro huésped activo con el email: "
                            + request.email()
            );
        }


        if (huespedRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono().trim(),
                EstadoRegistro.ACTIVO,
                id
        )) {

            throw new IllegalArgumentException(
                    "Ya existe otro huésped activo con el teléfono: "
                            + request.telefono()
            );
        }


        if (huespedRepository.existsByDocumentoIgnoreCaseAndEstadoRegistroAndIdNot(
                request.documento().trim(),
                EstadoRegistro.ACTIVO,
                id
        )) {

            throw new IllegalArgumentException(
                    "Ya existe otro huésped activo con el documento: "
                            + request.documento()
            );
        }
    }

}
    /*private void validarReservasActivas(Long idHuesped, String accion) {

        /*if (reservaClient.tieneReservaEnCursoHuesped(idHuesped)) {

            throw new IllegalStateException(
                    String.format(
                            "No se puede %s el huésped porque tiene una reserva EN_CURSO",
                            accion
                    )
            );
        }
    }
}*/
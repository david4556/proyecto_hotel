package com.davidpao.reservaciones.service;

import com.davidpao.commons.client.HabitacionClient;
import com.davidpao.commons.client.HuespedClient;
import com.davidpao.commons.dto.habitacion.HabitacionResponse;
import com.davidpao.commons.dto.huespedes.HuespedResponse;
import com.davidpao.commons.enums.EstadoRegistro;
import com.davidpao.commons.exceptions.RecursoNoEncontradoException;
import com.davidpao.reservaciones.dto.ReservacionRequest;
import com.davidpao.reservaciones.dto.ReservacionResponse;
import com.davidpao.reservaciones.entity.Reservacion;
import com.davidpao.reservaciones.enums.EstadoReservacion;
import com.davidpao.reservaciones.mapper.ReservacionMapper;
import com.davidpao.reservaciones.repository.ReservacionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ReservacionServiceImpl implements ReservacionService {

    private final ReservacionRepository reservacionRepository;
    private final ReservacionMapper reservacionMapper;
    private final HuespedClient huespedClient;
    private final HabitacionClient habitacionClient;

    private static final List<EstadoReservacion> ESTADOS_ACTIVOS = List.of(
            EstadoReservacion.CONFIRMADA,
            EstadoReservacion.EN_CURSO
    );

    private static final List<EstadoReservacion> ESTADOS_CONFIRMADA = List.of(
            EstadoReservacion.CONFIRMADA
    );


    @Override
    @Transactional(readOnly = true)
    public List<ReservacionResponse> listar() {

        log.info("Listando todas las reservaciones activas");

        return reservacionRepository
                .findByEstadoRegistro(EstadoRegistro.ACTIVO)
                .stream()
                .map(this::mapearReservacionConDetalles)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public ReservacionResponse obtenerPorId(Long id) {

        return mapearReservacionConDetalles(
                buscarReservacionActivaOExcepcion(id)
        );
    }


    @Override
    public ReservacionResponse registrar(
            ReservacionRequest request) {

        log.info("Registrando nueva reservación");

        /*
         * El huésped debe existir y estar ACTIVO.
         */
        HuespedResponse huesped =
                huespedClient.obtenerHuespedActivo(
                        request.idHuesped()
                );

        /*
         * La habitación debe existir y estar ACTIVA.
         */
        HabitacionResponse habitacion =
                obtenerHabitacionActiva(
                        request.idHabitacion()
                );

        /*
         * La habitación debe estar DISPONIBLE.
         */
        validarHabitacionDisponible(habitacion);

        /*
         * El huésped no puede tener otra
         * reservación activa.
         */
        validarSinReservacionActivaHuesped(
                request.idHuesped(),
                null
        );

        /*
         * La habitación no puede tener otra
         * reservación activa.
         */
        validarSinReservacionActivaHabitacion(
                request.idHabitacion(),
                null
        );

        /*
         * Reservacion.crear() establece:
         *
         * EstadoReserva.CONFIRMADA
         * EstadoRegistro.ACTIVO
         */
        Reservacion reservacion =
                reservacionRepository.save(
                        reservacionMapper.requestAEntidad(request)
                );

        /*
         * Al crear la reservación:
         *
         * DISPONIBLE -> OCUPADA
         */
        actualizarEstadoHabitacion(
                habitacion.id(),
                obtenerCodigoHabitacionOcupada()
        );

        log.info(
                "Reservación registrada exitosamente"
        );

        return reservacionMapper.entidadAResponse(
                reservacion,
                huesped,
                habitacion
        );
    }


    @Override
    @Transactional(readOnly = true)
    public boolean tieneReservacionConfirmadaOEnCursoHabitacion(
            Long idHabitacion) {

        return reservacionRepository
                .existsByIdHabitacionAndEstadoReservaIn(
                        idHabitacion,
                        List.of(
                                EstadoReservacion.CONFIRMADA,
                                EstadoReservacion.EN_CURSO
                        )
                );
    }


    @Override
    public ReservacionResponse actualizar(
            ReservacionRequest request,
            Long id) {

        log.info(
                "Actualizando reservación con ID: {}",
                id
        );

        Reservacion reservacion =
                buscarReservacionActivaOExcepcion(id);

        EstadoReservacion estadoActual = reservacion.getEstadoReserva();



        if (estadoActual == EstadoReservacion.CONFIRMADA) {

            if (!reservacion.getIdHuesped()
                    .equals(request.idHuesped())) {

                throw new IllegalStateException(
                        "La reservación confirmada no puede cambiar de huésped"
                );
            }

            if (!reservacion.getIdHabitacion()
                    .equals(request.idHabitacion())) {

                throw new IllegalStateException(
                        "La reservación confirmada no puede cambiar de habitación"
                );
            }

            reservacion.actualizar(
                    request.idHuesped(),
                    request.idHabitacion(),
                    request.fechaEntrada(),
                    request.fechaSalida()
            );
        }



        else if (estadoActual == EstadoReserva.EN_CURSO) {

            if (!reservacion.getIdHuesped()
                    .equals(request.idHuesped())) {

                throw new IllegalStateException(
                        "La reservación en curso no puede cambiar de huésped"
                );
            }

            if (!reservacion.getIdHabitacion()
                    .equals(request.idHabitacion())) {

                throw new IllegalStateException(
                        "La reservación en curso no puede cambiar de habitación"
                );
            }

            if (!reservacion.getFechaEntrada()
                    .equals(request.fechaEntrada())) {

                throw new IllegalStateException(
                        "La reservación en curso no puede modificar la fecha de entrada"
                );
            }

            reservacion.actualizarFechaSalida(
                    request.fechaSalida()
            );
        }



        else {

            throw new IllegalStateException(
                    "La reservación con estado "
                            + estadoActual
                            + " no puede modificarse"
            );
        }

        HuespedResponse huesped = huespedClient.obtenerHuespedActivo(reservacion.getIdHuesped());

        HabitacionResponse habitacion = habitacionClient.obtenerHabitacionSinValidarEstado(
                        reservacion.getIdHabitacion()
        );

        log.info(
                "Reservación actualizada correctamente"
        );

        return reservacionMapper.entidadAResponse(
                reservacion,
                huesped,
                habitacion
        );
    }


    @Override
    public void actualizarEstadoReservacion(
            Long idReservacion,
            Long idEstadoReservacion) {

        Reservacion reservacion =
                buscarReservacionActivaOExcepcion(
                        idReservacion
                );

        EstadoReservacion nuevoEstado =
                EstadoReservacion.obtenerEstadoReservaPorCodigo(
                        idEstadoReservacion);


        reservacion.actualizarEstadoReserva(nuevoEstado);


        gestionarEstadoHabitacionPorCambioReserva(
                reservacion.getIdHabitacion(),
                nuevoEstado
        );

        log.info(
                "Estado de reservación {} actualizado a {}",
                idReservacion,
                nuevoEstado
        );
    }


    @Override
    public void eliminar(Long id) {

        Reservacion reservacion =
                buscarReservacionActivaOExcepcion(id);

        if (!reservacion.getEstadoReserva()
                .isEliminable()) {

            throw new IllegalStateException(
                    "La reservación no puede eliminarse en su estado actual"
            );
        }


        if (reservacion.getEstadoReserva()
                == EstadoReservacion.CONFIRMADA) {

            actualizarEstadoHabitacion(
                    reservacion.getIdHabitacion(),
                    obtenerCodigoHabitacionDisponible()
            );
        }

        reservacion.eliminar();

        log.info(
                "Reservación {} eliminada lógicamente",
                id
        );
    }


    private void gestionarEstadoHabitacionPorCambioReserva(
            Long idHabitacion,
            EstadoReservacion estado) {

        Long nuevoEstado = switch (estado) {

            case EN_CURSO ->
                    obtenerCodigoHabitacionOcupada();

            case FINALIZADA ->
                    obtenerCodigoHabitacionDisponible();

            case CANCELADA ->
                    obtenerCodigoHabitacionDisponible();

            default -> null;
        };

        if (nuevoEstado != null) {

            actualizarEstadoHabitacion(
                    idHabitacion,
                    nuevoEstado
            );
        }
    }


    private Reservacion buscarReservacionActivaOExcepcion(
            Long id) {

        return reservacionRepository
                .findByIdAndEstadoRegistro(
                        id,
                        EstadoRegistro.ACTIVO
                )
                .orElseThrow(
                        () -> new RecursoNoEncontradoException(
                                "Reservación no encontrada con ID: " + id
                        )
                );
    }


    private ReservacionResponse mapearReservacionConDetalles(
            Reservacion reservacion) {

        return reservacionMapper.entidadAResponse(
                reservacion,
                huespedClient.obtenerHuespedSinValidarEstado(
                        reservacion.getIdHuesped()
                ),
                habitacionClient.obtenerHabitacionSinValidarEstado(
                        reservacion.getIdHabitacion()
                )
        );
    }


    private void validarSinReservacionActivaHuesped(
            Long idHuesped,
            Long idReservacionExcluir) {

        boolean tieneReservacion =
                (idReservacionExcluir == null)

                        ? reservacionRepository
                        .existsByIdHuespedAndEstadoReservaIn(
                                idHuesped,
                                ESTADOS_ACTIVOS
                        )

                        : reservacionRepository
                        .existsByIdHuespedAndEstadoReservaInAndIdNot(
                                idHuesped,
                                ESTADOS_ACTIVOS,
                                idReservacionExcluir
                        );

        if (tieneReservacion) {

            throw new IllegalStateException(
                    "El huésped ya tiene una reservación activa"
            );
        }
    }


    private void validarSinReservacionActivaHabitacion(
            Long idHabitacion,
            Long idReservacionExcluir) {

        boolean tieneReservacion =
                (idReservacionExcluir == null)

                        ? reservacionRepository
                        .existsByIdHabitacionAndEstadoReservaIn(
                                idHabitacion,
                                ESTADOS_ACTIVOS
                        )

                        : reservacionRepository
                        .existsByIdHabitacionAndEstadoReservaInAndIdNot(
                                idHabitacion,
                                ESTADOS_ACTIVOS,
                                idReservacionExcluir
                        );

        if (tieneReservacion) {

            throw new IllegalStateException(
                    "La habitación ya tiene una reservación activa"
            );
        }
    }


    private HabitacionResponse obtenerHabitacionActiva(
            Long id) {

        return habitacionClient.obtenerHabitacionActivaPorId(id);
    }


    private void actualizarEstadoHabitacion(
            Long idHabitacion,
            Long idEstado) {

        habitacionClient.actualizarEstadoHabitacion(
                idHabitacion,
                idEstado
        );
    }


    private void validarHabitacionDisponible(
            HabitacionResponse habitacion) {

        if (!habitacion.idEstadoHabitacion()
                .equals(
                        obtenerCodigoHabitacionDisponible()
                )) {

            throw new IllegalStateException(
                    "La habitación no está disponible"
            );
        }
    }


    private Long obtenerCodigoHabitacionDisponible() {
        return 1L;
    }


    private Long obtenerCodigoHabitacionOcupada() {
        return 2L;
    }
}
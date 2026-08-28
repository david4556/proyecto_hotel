package com.davidpao.reservaciones.service;

import com.davidpao.commons.client.HabitacionClient;
import com.davidpao.commons.client.HuespedClient;
import com.davidpao.commons.dto.habitacion.HabitacionResponse;
import com.davidpao.commons.dto.huespedes.HuespedResponse;
import com.davidpao.commons.enums.EstadoHabitacion;
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

import java.time.LocalDate;
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

        Reservacion reservacion =  obtenerReservacionOException(id);

        return reservacionMapper.entidadAResponse
                (reservacion,
                        obtenerHuespedSinEstado(reservacion.getIdHuesped()),
                        obtenerHabitacionSinEstado(reservacion.getIdHabitacion()));
    }

    @Override
    public boolean tieneReservasEnCurso(Long idHuesped) {
        return reservacionRepository.existsByIdHuespedAndEstadoReserva(idHuesped, EstadoReservacion.EN_CURSO);
    }

    @Override
    public ReservacionResponse registrar(ReservacionRequest request) {

        log.info("Iniciando creación de reservación para huésped ID: {} y habitación ID: {}",
                request.idHuesped(), request.idHabitacion());


        validarRangoFechas(request.fechaEntrada(), request.fechaSalida());

        HuespedResponse huesped = obtenerHuespedActivo(request.idHuesped());

        HabitacionResponse habitacion = obtenerHabitacionActiva(request.idHabitacion());

        Reservacion reservacion = Reservacion.crear(
                request.idHuesped(),
                request.idHabitacion(),
                request.fechaEntrada(),
                request.fechaSalida()
        );

        reservacionRepository.save(reservacion);

        sincronizarDisponibilidadHabitacion(reservacion.getIdHabitacion(), EstadoHabitacion.OCUPADA);

        log.info("Reservación registrada exitosamente con id: {}", reservacion.getId());

        return reservacionMapper.entidadAResponse(
                reservacion,
                huesped,
                habitacion
        );
    }

    @Override
    public ReservacionResponse actualizar(ReservacionRequest request, Long id) {

        log.info("Iniciando actualización de la reservación con id: {}", id);

        Reservacion reservacion = obtenerReservacionOException(id);
        EstadoReservacion estadoActual = reservacion.getEstadoReserva();

        validarEstadoPermiteModificacion(estadoActual);

        Long idHabitacionAnterior = reservacion.getIdHabitacion();
        Long idNuevaHabitacion = request.idHabitacion();
        boolean cambioDeHabitacion = !idHabitacionAnterior.equals(idNuevaHabitacion);

        if (EstadoReservacion.EN_CURSO.equals(estadoActual)) {
            if (cambioDeHabitacion)
                throw new IllegalStateException("No se puede cambiar de habitación en una reserva EN_CURSO (con Check-in realizado).");

            if (!reservacion.getIdHuesped().equals(request.idHuesped()))
                throw new IllegalStateException("No se puede cambiar de huésped en una reserva EN_CURSO.");

            if (!reservacion.getFechaEntrada().equals(request.fechaEntrada()))
                throw new IllegalStateException("No se puede modificar la fecha de entrada en una reserva EN_CURSO.");


        validarRangoFechas(reservacion.getFechaEntrada(), request.fechaSalida());

    } else if (EstadoReservacion.CONFIRMADA.equals(estadoActual)) {
        validarRangoFechas(request.fechaEntrada(), request.fechaSalida());

        if (cambioDeHabitacion) {
            HabitacionResponse nuevaHabitacion = obtenerHabitacionActiva(idNuevaHabitacion);
            validarHabitacionDisponible(nuevaHabitacion);

            sincronizarDisponibilidadHabitacion(idHabitacionAnterior, EstadoHabitacion.DISPONIBLE);

            sincronizarDisponibilidadHabitacion(idNuevaHabitacion, EstadoHabitacion.OCUPADA);
        }
    }

        HuespedResponse huesped = obtenerHuespedActivo(request.idHuesped());
        HabitacionResponse habitacionFinal = obtenerHabitacionActiva(idNuevaHabitacion);

        reservacion.actualizar(
                request.idHuesped(),
                idNuevaHabitacion,
                request.fechaEntrada(),
                request.fechaSalida()
        );

        reservacionRepository.saveAndFlush(reservacion);
        log.info("Reservación id: {} actualizada exitosamente.", id);

        return reservacionMapper.entidadAResponse(reservacion, huesped, habitacionFinal);

    }

    @Override
    public void eliminar(Long id) {

        Reservacion reservacion = obtenerReservacionOException(id);

        log.info("Iniciando eliminación lógica de reservación con id: {}", id);

        if (reservacion.getEstadoReserva() == EstadoReservacion.EN_CURSO)
            throw new IllegalStateException("No se puede eliminar una reservación en estado EN_CURSO (Check-in realizado).");

        reservacion.eliminar();

        reservacionRepository.save(reservacion);

        if (reservacion.getEstadoReserva() == EstadoReservacion.CONFIRMADA) {
            sincronizarDisponibilidadHabitacion(
                    reservacion.getIdHabitacion(),
                    EstadoHabitacion.DISPONIBLE
            );
            log.info("Habitación id: {} liberada a DISPONIBLE tras eliminación de reserva confirmada.", reservacion.getIdHabitacion());
        }

        log.info("Reservación con id {} ha sido marcada como eliminada exitosamente.", id);

    }

    @Override
    @Transactional
    public void actualizarEstadoReservacion(Long idReservacion, Long idEstadoReservacion) {
        log.info("Actualizando estado de la reservación ID: {} a código de estado: {}", idReservacion, idEstadoReservacion);

        Reservacion reservacion = obtenerReservacionOException(idReservacion);
        EstadoReservacion nuevoEstado = EstadoReservacion.obtenerEstadoReservaPorCodigo(idEstadoReservacion);

        validarTransicionDeEstado(reservacion.getEstadoReserva(), nuevoEstado);

        reservacion.actualizarEstadoReservacion(nuevoEstado);
        reservacionRepository.save(reservacion);

        sincronizarDisponibilidadHabitacion(reservacion.getIdHabitacion(), nuevoEstado);

        log.info("Estado de reservación ID: {} actualizado con éxito a {}", idReservacion, nuevoEstado);
    }


    private ReservacionResponse mapearReservacionConDetalles(
            Reservacion reservacion) {

        return reservacionMapper.entidadAResponse(
                reservacion,
                huespedClient.obtenerHuespedPorIdSinEstado(
                        reservacion.getIdHuesped()
                ),
                habitacionClient.obtenerHabitacionPorIdSinEstado(
                        reservacion.getIdHabitacion()
                )
        );
    }

    private Reservacion obtenerReservacionOException(Long id){
        log.info("Buscando cita con id {} ...",id);

        return reservacionRepository.findById(id).orElseThrow(()->
                new RecursoNoEncontradoException("Reservacion no encontrada con id: "+id));
    }

    private HuespedResponse obtenerHuespedSinEstado(Long id){
        log.info("Buscando huesped sin activo con id {} en el servicio remoto...",id);

        return huespedClient.obtenerHuespedPorIdSinEstado(id);
    }


    private HabitacionResponse obtenerHabitacionSinEstado(Long id){
        log.info("Buscando habitacion sin activo con id {} en el servicio remoto...",id);

        return habitacionClient.obtenerHabitacionPorIdSinEstado(id);
    }

    private HuespedResponse obtenerHuespedActivo(Long id){
        log.info("Buscando huesped activo con id {} en el servicio remoto...",id);

        return huespedClient.obtenerHuespedActivoPorId(id);
    }

    private HabitacionResponse obtenerHabitacionActiva(Long id){
        log.info("Buscando habitacion activa con id {} en el servicio remoto...",id);

        return habitacionClient.obtenerHabitacionActivoPorId(id);
    }
    private void validarHabitacionDisponible(HabitacionResponse habitacion) {
        EstadoHabitacion estado = EstadoHabitacion.valueOf(habitacion.estadoHabitacion().toUpperCase());

        if (estado != EstadoHabitacion.DISPONIBLE) {
            throw new IllegalStateException(
                    "La habitación " + habitacion.numeroHabitacion() + " no está DISPONIBLE para reservar."
            );
        }
    }

    private void validarRangoFechas(LocalDate fechaEntrada, LocalDate fechaSalida) {
        if (fechaEntrada == null || fechaSalida == null)
            throw new IllegalArgumentException("Las fechas de entrada y salida son obligatorias.");

        if (!fechaEntrada.isBefore(fechaSalida))
            throw new IllegalArgumentException("La fecha de entrada debe ser estrictamente menor a la fecha de salida.");

    }



    private void sincronizarDisponibilidadHabitacion(Long idHabitacion, EstadoHabitacion nuevoEstado) {
        log.info("Sincronizando estado de habitación {} a {}", idHabitacion, nuevoEstado);
        habitacionClient.actualizarDisponibilidadHabitacion(idHabitacion, nuevoEstado.getCodigo());
    }

    private void validarEstadoPermiteModificacion(EstadoReservacion estado) {
        if (EstadoReservacion.FINALIZADA.equals(estado) || EstadoReservacion.CANCELADA.equals(estado)) {
            throw new IllegalStateException("No se permite modificar una reserva " + estado.name() + " (solo consulta histórica).");
        }

    }

    private void validarTransicionDeEstado(EstadoReservacion actual, EstadoReservacion nuevo) {
        if (actual == nuevo) {
            throw new IllegalArgumentException(
                    "La reservación ya se encuentra en el estado " + actual.name() + " (" + actual.getDescripcion() + ")"
            );
        }

        boolean esTransicionValida = switch (actual) {
            case CONFIRMADA -> nuevo == EstadoReservacion.EN_CURSO || nuevo == EstadoReservacion.CANCELADA;
            case EN_CURSO   -> nuevo == EstadoReservacion.FINALIZADA;
            case FINALIZADA, CANCELADA -> false; // Estados terminales
        };

        if (!esTransicionValida) {
            throw new IllegalStateException(
                    String.format("Transición de estado no permitida: no se puede pasar de %s a %s.", actual.name(), nuevo.name())
            );
        }
    }

        private void sincronizarDisponibilidadHabitacion(Long idHabitacion, EstadoReservacion nuevoEstadoReserva) {
            Long idDisponibilidad = switch (nuevoEstadoReserva) {
                case CONFIRMADA, EN_CURSO -> EstadoHabitacion.OCUPADA.getCodigo();
                case FINALIZADA, CANCELADA -> EstadoHabitacion.DISPONIBLE.getCodigo();
            };

            log.info("Sincronizando habitación ID: {} a disponibilidad ID: {} por reserva en {}",
                    idHabitacion, idDisponibilidad, nuevoEstadoReserva);

            habitacionClient.actualizarDisponibilidadHabitacion(idHabitacion, idDisponibilidad);
        }

}
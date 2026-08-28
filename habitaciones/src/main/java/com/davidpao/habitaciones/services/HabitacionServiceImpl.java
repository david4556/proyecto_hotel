package com.davidpao.habitaciones.services;

import com.davidpao.commons.dto.habitacion.HabitacionRequest;
import com.davidpao.commons.dto.habitacion.HabitacionResponse;
import com.davidpao.commons.enums.EstadoHabitacion;
import com.davidpao.commons.enums.EstadoRegistro;
import com.davidpao.commons.enums.TipoHabitacion;
import com.davidpao.commons.exceptions.RecursoNoEncontradoException;
import com.davidpao.habitaciones.entity.Habitacion;
import com.davidpao.habitaciones.mappers.HabitacionMapper;
import com.davidpao.habitaciones.repository.HabitacionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
@Slf4j

public class HabitacionServiceImpl implements HabitacionService{
    @Override
    public HabitacionResponse obtenerHabitacionPorIdSinEstado(Long id) {
        return null;
    }

    private final HabitacionRepository habitacionRepository;

    private final HabitacionMapper habitacionMapper;

    @Override
    public List<HabitacionResponse> listar() {
        log.info("Listando habitaciones con estado ACTIVO");
        return habitacionRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO)
                .stream()
                .map(habitacionMapper::entidadAResponse)
                .toList();
    }

    @Override
    public HabitacionResponse obtenerPorId(Long id) {
        return habitacionMapper.entidadAResponse(obtenerHabitacionActivoOException(id));
    }

    @Override
    public HabitacionResponse registrar(HabitacionRequest request) {
        log.info("Registrando nueva habitacion: {}",request.numeroHabitacion());

        validarDatosUnicos(request);

        Habitacion habitacion =  habitacionMapper.requestAEntidad(request);

        habitacion.actualizarTipoHabitacion(TipoHabitacion.obtenerDisponibilidadPorCodigo(request.idTipoHabitacion()));

        habitacionRepository.save(habitacion);

        return habitacionMapper.entidadAResponse(habitacion);

    }

    @Override
    public HabitacionResponse actualizar(HabitacionRequest request, Long id) {
        Habitacion habitacion = obtenerHabitacionActivaOException(id);

        log.info("Actualizar habitación con id: {}", id);

        validarCambiosUnicos(request, id);

        habitacion.actualizar(
                request.numeroHabitacion(),
                TipoHabitacion.obtenerTipoHabitacionPorCodigo(request.idTipoHabitacion()),
                request.precio(),
                request.capacidad(),
                habitacion.getEstadoHabitacion(),
                habitacion.getEstadoRegistro()
        );

        Habitacion habitacionActualizada = habitacionRepository.save(habitacion);

        log.info("Habitación actualizada exitosamente con id: {}", id);

        return habitacionMapper.entidadAResponse(habitacionActualizada);
    }

    @Override
    public void eliminar(Long id) {

        log.info("Iniciando proceso de eliminación de habitación con id: {}", id);

        Habitacion habitacion = obtenerHabitacionActivoOException(id);

        validarNoEsteOcupada(habitacion);

        log.info("Eliminando habitación con id: {}", id);
        habitacion.eliminar(); // O habitacion.setEstadoRegistro(EstadoRegistro.INACTIVO);

        habitacionRepository.save(habitacion);
    }


    @Override
    public void actualizarEstadoHabitacion(Long idHabitacion, Long idEstadoHabitacion) {
        log.info("Solicitud para actualizar estado de la habitación con id: {} al estado id: {}", idHabitacion, idEstadoHabitacion);

        Habitacion habitacion = obtenerHabitacionActivaOException(idHabitacion);

        EstadoHabitacion nuevoEstado = EstadoHabitacion.obtenerEstadoHabitacionPorCodigo(idEstadoHabitacion);

        validarTransicionDeEstado(habitacion.getEstadoHabitacion(), nuevoEstado);

        log.info("Cambiando estado de {} a {}", habitacion.getEstadoHabitacion(), nuevoEstado);
        habitacion.actualizarDisponibilidadHabitacion(nuevoEstado);

        habitacionRepository.save(habitacion);
    }

    private Habitacion obtenerHabitacionActivoOException(Long id){
        log.info("Listando habitacion con estado ACTIVO");

        return habitacionRepository.findByIdAndEstadoRegistro(id,EstadoRegistro.ACTIVO)
                .orElseThrow(()-> new RecursoNoEncontradoException("Habitacion Activa no encontrada con id:"+id));

    }


    private void validarDatosUnicos(HabitacionRequest request) {
        log.info("Validando número de habitación único entre activos: {}", request.numeroHabitacion());

        if (habitacionRepository.existsByNumeroHabitacionAndEstadoRegistro(
                request.numeroHabitacion(), EstadoRegistro.ACTIVO)) {
            throw new IllegalArgumentException(
                    "Ya existe una habitación activa registrada con el número: " + request.numeroHabitacion());
        }
    }

    private Habitacion obtenerHabitacionActivaOException(Long id) {
        return habitacionRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró una habitación activa con ID: " + id));
    }

    private void validarNoEsteOcupada(Habitacion habitacion) {
        log.info("Validando que la habitación {} no esté OCUPADA...", habitacion.getNumeroHabitacion());

        if (EstadoHabitacion.OCUPADA.equals(habitacion.getEstadoHabitacion())) {
            throw new IllegalStateException(
                    "No se puede eliminar la habitación " + habitacion.getNumeroHabitacion() + " porque está OCUPADA."
            );
        }
    }

    private void validarCambiosUnicos(HabitacionRequest request, Long id) {
        log.info("Validando si el número de habitación ya existe en otro registro activo...");

        habitacionRepository.findByNumeroHabitacionAndEstadoRegistro(request.numeroHabitacion(), EstadoRegistro.ACTIVO)
                .ifPresent(habitacionExistente -> {
                    if (!habitacionExistente.getId().equals(id)) {
                        throw new IllegalArgumentException(
                                "Ya existe otra habitación activa registrada con el número: " + request.numeroHabitacion()
                        );
                    }
                });
    }

    private void validarTransicionDeEstado(EstadoHabitacion estadoActual, EstadoHabitacion nuevoEstado) {
        if (estadoActual == nuevoEstado) {
            throw new IllegalArgumentException(
                    "La habitación ya se encuentra en el estado " + estadoActual.name() + " (" + estadoActual.getDescripcion() + ")"
            );
        }

        if (EstadoHabitacion.OCUPADA.equals(estadoActual) && EstadoHabitacion.DISPONIBLE.equals(nuevoEstado)) {
            throw new IllegalStateException(
                    "No se puede cambiar manualmente a DISPONIBLE si la habitación está OCUPADA."
            );
        }
    }


}

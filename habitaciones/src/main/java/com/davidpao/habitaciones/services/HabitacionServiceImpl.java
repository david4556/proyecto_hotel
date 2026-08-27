package com.davidpao.habitaciones.services;

import com.davidpao.commons.dto.habitacion.HabitacionRequest;
import com.davidpao.commons.dto.habitacion.HabitacionResponse;
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
        return null;
    }

    @Override
    public void eliminar(Long id) {

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
}

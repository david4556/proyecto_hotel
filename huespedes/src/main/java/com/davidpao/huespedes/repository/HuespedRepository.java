package com.davidpao.huespedes.repository;

import com.davidpao.commons.enums.EstadoRegistro;
import com.davidpao.huespedes.entity.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HuespedRepository extends JpaRepository<Huesped, Long> {

    List<Huesped> findByEstadoRegistro(EstadoRegistro estadoRegistro);

    Optional<Huesped> findByIdAndEstadoRegistro(
            Long id,
            EstadoRegistro estadoRegistro
    );

    boolean existsByEmailIgnoreCaseAndEstadoRegistro(
            String email,
            EstadoRegistro estadoRegistro
    );

    boolean existsByTelefonoAndEstadoRegistro(
            String telefono,
            EstadoRegistro estadoRegistro
    );

    boolean existsByDocumentoIgnoreCaseAndEstadoRegistro(
            String documento,
            EstadoRegistro estadoRegistro
    );

    boolean existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
            String email,
            EstadoRegistro estadoRegistro,
            Long id
    );

    boolean existsByTelefonoAndEstadoRegistroAndIdNot(
            String telefono,
            EstadoRegistro estadoRegistro,
            Long id
    );

    boolean existsByDocumentoIgnoreCaseAndEstadoRegistroAndIdNot(
            String documento,
            EstadoRegistro estadoRegistro,
            Long id
    );
}
package com.davidpao.huespedes.entity;

import com.davidpao.commons.enums.EstadoRegistro;
import com.davidpao.commons.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "HUESPEDES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Huesped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HUESPED")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "TELEFONO", nullable = false, length = 10)
    private String telefono;

    @Column(name = "DOCUMENTO", nullable = false, length = 50)
    private String documento;

    @Column(name = "NACIONALIDAD", nullable = false, length = 25)
    private String nacionalidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false, length = 30)
    @Builder.Default
    private EstadoRegistro estadoRegistro = EstadoRegistro.ACTIVO;


    public void validarDatos(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            String email,
            String telefono,
            String documento,
            String nacionalidad
    ) {

        StringCustomUtils.validarTamanio(nombre, 2, 50,
                "El nombre es requerido y debe tener entre 2 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoPaterno, 2, 50,
                "El apellido paterno es requerido y debe tener entre 2 y 50 caracteres");

        StringCustomUtils.validarTamanio(apellidoMaterno, 2, 50,
                "El apellido materno es requerido y debe tener entre 2 y 50 caracteres");

        StringCustomUtils.validarTamanio(email, 1, 100,
                "El email es requerido y debe tener entre 1 y 100 caracteres");

        StringCustomUtils.validarTamanio(
                telefono,
                10,
                10,
                "El teléfono debe contener exactamente 10 dígitos"
        );

        StringCustomUtils.validarTamanio(
                documento,
                1,
                50,
                "El documento es requerido y debe tener entre 1 y 50 caracteres"
        );

        StringCustomUtils.validarTamanio(
                nacionalidad,
                1,
                25,
                "La nacionalidad es requerida y debe tener entre 1 y 25 caracteres"
        );
    }


    private void validarNoEliminado() {
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO) {
            throw new IllegalArgumentException("El huésped ya está eliminado");
        }
    }


    public void actualizar(
            String nombre,
            String apellidoPaterno,
            String apellidoMaterno,
            String email,
            String telefono,
            String documento,
            String nacionalidad
    ) {

        validarDatos(
                nombre,
                apellidoPaterno,
                apellidoMaterno,
                email,
                telefono,
                documento,
                nacionalidad
        );

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.email = email.trim();
        this.telefono = telefono.trim();
        this.documento = documento.trim();
        this.nacionalidad = nacionalidad.trim();
    }


    public void eliminar() {
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }
}
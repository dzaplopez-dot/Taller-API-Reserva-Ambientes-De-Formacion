package com.agendasena.agendasena.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ambientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ambiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Enumerated(EnumType.STRING)  // ← ¡CORREGIDO!
    @Column(nullable = false)
    private TipoAmbiente tipo;    // ← Asegúrate que TipoAmbiente existe

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false)
    private Boolean activo = true;
}



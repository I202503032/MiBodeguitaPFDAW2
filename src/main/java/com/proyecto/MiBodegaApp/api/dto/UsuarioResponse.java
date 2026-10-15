package com.proyecto.MiBodegaApp.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class UsuarioResponse {
    private Integer id;
    private String nombre;
    private String email;
    private String tipo;
}

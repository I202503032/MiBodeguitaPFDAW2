package com.proyecto.MiBodegaApp.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductoRequest {
    @NotBlank private String nombre;
    private String descripcion;
    private String imagen;
    @NotNull @DecimalMin(value = "0.0", inclusive = false) private Double precio;
    @NotNull @Min(0) private Integer cantidad;
    @NotNull @Min(0) private Integer stockMinimo;
    private Integer usuarioId;
}

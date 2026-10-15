package com.proyecto.MiBodegaApp.api.dto;

import com.proyecto.MiBodegaApp.model.Producto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class ProductoResponse {
    private Integer id;
    private String nombre;
    private String descripcion;
    private String imagen;
    private double precio;
    private int cantidad;
    private Integer usuarioId;

    public static ProductoResponse from(Producto p) {
        return new ProductoResponse(p.getId(), p.getNombre(), p.getDescripcion(), p.getImagen(), p.getPrecio(), p.getCantidad(), p.getUsuario() == null ? null : p.getUsuario().getId());
    }
}

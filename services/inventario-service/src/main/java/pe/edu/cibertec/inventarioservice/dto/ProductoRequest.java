
package pe.edu.cibertec.inventarioservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductoRequest(
        @NotBlank String nombre,
        String descripcion,
        String imagen,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) Double precio,
        @NotNull @Min(0) Integer cantidad,
        @NotNull @Min(0) Integer stockMinimo,
        Integer usuarioId
) {
}

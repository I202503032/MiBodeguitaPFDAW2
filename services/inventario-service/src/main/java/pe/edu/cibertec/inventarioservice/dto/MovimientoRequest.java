package pe.edu.cibertec.inventarioservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MovimientoRequest(
        @NotBlank String tipo,
        @NotNull @Min(1) Integer cantidad,
        @NotBlank String motivo,
        Integer usuarioId
) {
}

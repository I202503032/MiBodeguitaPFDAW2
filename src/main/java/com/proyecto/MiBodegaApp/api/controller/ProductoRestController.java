package com.proyecto.MiBodegaApp.api.controller;

import com.proyecto.MiBodegaApp.api.dto.ProductoRequest;
import com.proyecto.MiBodegaApp.api.dto.ProductoResponse;
import com.proyecto.MiBodegaApp.model.MovimientoInventario;
import com.proyecto.MiBodegaApp.model.Producto;
import com.proyecto.MiBodegaApp.model.Usuario;
import com.proyecto.MiBodegaApp.repository.IMovimientoInventarioRepository;
import com.proyecto.MiBodegaApp.repository.IUsuarioRepository;
import com.proyecto.MiBodegaApp.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "${app.cors.allowed-origin:http://localhost:4200}")
@RequestMapping("/api/productos")
public class ProductoRestController {
    private final ProductoService productoService;
    private final IUsuarioRepository usuarioRepository;
    private final IMovimientoInventarioRepository movimientoRepository;

    public ProductoRestController(ProductoService productoService,
                                  IUsuarioRepository usuarioRepository,
                                  IMovimientoInventarioRepository movimientoRepository) {
        this.productoService = productoService;
        this.usuarioRepository = usuarioRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @GetMapping
    public List<ProductoResponse> listar() {
        return productoService.findAll().stream().map(ProductoResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return productoService.get(id).<ResponseEntity<?>>map(p -> ResponseEntity.ok(ProductoResponse.from(p)))
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Producto no encontrado")));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ProductoRequest request) {
        Producto producto = new Producto();
        copiar(request, producto);
        asignarUsuario(request, producto);
        Producto guardado = productoService.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductoResponse.from(guardado));
    }

    @PostMapping("/{id}/movimiento")
    public ResponseEntity<?> registrarMovimiento(@PathVariable Integer id, @RequestParam String tipo, @RequestParam int cantidad, @RequestParam String motivo, @RequestParam(required = false) Integer usuarioId) {
        return productoService.get(id).<ResponseEntity<?>>map(producto -> {
            if ("SALIDA".equalsIgnoreCase(tipo)) {
                if (producto.getCantidad() < cantidad) {
                    return ResponseEntity.badRequest().body(Map.of("mensaje", "Stock insuficiente. No se permite stock negativo. Stock actual: " + producto.getCantidad()));
                }
                producto.setCantidad(producto.getCantidad() - cantidad);
            } else if ("ENTRADA".equalsIgnoreCase(tipo)) {
                producto.setCantidad(producto.getCantidad() + cantidad);
            } else {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "Tipo de movimiento inválido (Use ENTRADA o SALIDA)"));
            }

            productoService.update(producto);

            // Registrar historial de movimiento
            MovimientoInventario mov = new MovimientoInventario();
            mov.setTipo(tipo.toUpperCase());
            mov.setCantidad(cantidad);
            mov.setMotivo(motivo);
            mov.setProducto(producto);
            if (usuarioId != null) {
                usuarioRepository.findById(usuarioId).ifPresent(mov::setUsuario);
            }

            movimientoRepository.save(mov);

            return ResponseEntity.ok(ProductoResponse.from(producto));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Producto no encontrado")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody ProductoRequest request) {
        return productoService.get(id).<ResponseEntity<?>>map(existente -> {
            copiar(request, existente);
            asignarUsuario(request, existente);
            productoService.update(existente);
            return ResponseEntity.ok(ProductoResponse.from(existente));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Producto no encontrado")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        if (productoService.get(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Producto no encontrado"));
        }
        productoService.delate(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> manejarSolicitudInvalida(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("mensaje", ex.getMessage()));
    }

    private void copiar(ProductoRequest request, Producto producto) {
        producto.setNombre(request.getNombre().trim());
        producto.setDescripcion(request.getDescripcion());
        producto.setImagen(request.getImagen() == null || request.getImagen().isBlank() ? "default.jpg" : request.getImagen());
        producto.setPrecio(request.getPrecio());
        producto.setCantidad(request.getCantidad());
        if (request.getStockMinimo() != null) {
            producto.setStockMinimo(request.getStockMinimo()); // <- Añadir esta línea para persistir el stock mínimo
        }
    }

    private void asignarUsuario(ProductoRequest request, Producto producto) {
        if (request.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("El usuario indicado no existe"));
            producto.setUsuario(usuario);
        }
    }
}

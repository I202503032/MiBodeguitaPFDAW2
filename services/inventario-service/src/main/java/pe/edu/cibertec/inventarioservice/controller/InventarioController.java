package pe.edu.cibertec.inventarioservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.inventarioservice.dto.MovimientoRequest;
import pe.edu.cibertec.inventarioservice.dto.ProductoRequest;
import pe.edu.cibertec.inventarioservice.model.MovimientoInventario;
import pe.edu.cibertec.inventarioservice.model.Producto;
import pe.edu.cibertec.inventarioservice.service.InventarioService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "${app.cors.allowed-origin:http://localhost:4200}")
public class InventarioController {
    private final InventarioService service;
    public InventarioController(InventarioService service) { this.service=service; }
    @GetMapping public List<Producto> listar() { return service.listar(); }
    @GetMapping("/{id}") public Producto obtener(@PathVariable Integer id) { return service.obtener(id); }
    @PostMapping public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request)); }
    @PutMapping("/{id}") public Producto actualizar(@PathVariable Integer id, @Valid @RequestBody ProductoRequest request) { return service.actualizar(id,request); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Integer id) { service.eliminar(id); return ResponseEntity.noContent().build(); }
    @PostMapping("/{id}/movimiento") public Producto movimiento(@PathVariable Integer id, @Valid @RequestBody MovimientoRequest request) { return service.registrarMovimiento(id,request); }
    @GetMapping("/{id}/movimientos") public List<MovimientoInventario> movimientos(@PathVariable Integer id) { return service.listarMovimientos(id); }
    @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<Map<String,String>> error(IllegalArgumentException ex) { return ResponseEntity.badRequest().body(Map.of("mensaje",ex.getMessage())); }
}

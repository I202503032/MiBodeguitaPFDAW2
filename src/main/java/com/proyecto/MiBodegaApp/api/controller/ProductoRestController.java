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
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.Color;
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
    @GetMapping("/exportar")
    public void exportarAExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=inventario_mibodeguita.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventario Productos");

        Row headerRow = sheet.createRow(0);
        String[] columnas = {"ID", "Nombre", "Descripción", "Precio (S/)", "Stock", "Stock Mínimo"};

        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < columnas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        List<Producto> listaProductos = productoService.findAll();

        int rowIdx = 1;
        for (Producto producto : listaProductos) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(producto.getId());
            row.createCell(1).setCellValue(producto.getNombre());
            row.createCell(2).setCellValue(producto.getDescripcion());
            row.createCell(3).setCellValue(producto.getPrecio());
            row.createCell(4).setCellValue(producto.getCantidad());
            row.createCell(5).setCellValue(producto.getStockMinimo() != null ? producto.getStockMinimo() : 0);
        }

        for (int i = 0; i < columnas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
    @GetMapping("/exportar/pdf")
    public void exportarAPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=inventario_mibodeguita.pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Paragraph title = new Paragraph("Reporte de Inventario - MiBodeguita", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        PdfPTable table = new PdfPTable(5); // 5 columnas
        table.setWidthPercentage(100);
        table.addCell("ID");
        table.addCell("Producto");
        table.addCell("Precio (S/)");
        table.addCell("Stock");
        table.addCell("Stock Mín.");

        List<Producto> listaProductos = productoService.findAll();
        for (Producto p : listaProductos) {
            table.addCell(String.valueOf(p.getId()));
            table.addCell(p.getNombre());
            table.addCell(String.valueOf(p.getPrecio()));
            table.addCell(String.valueOf(p.getCantidad()));
            table.addCell(String.valueOf(p.getStockMinimo() != null ? p.getStockMinimo() : 0));
        }

        document.add(table);
        document.close();
    }

    // 📋 CSV ENDPOINT (Texto Plano)
    @GetMapping("/exportar/csv")
    public void exportarACSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=inventario_mibodeguita.csv");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Producto,Descripcion,Precio,Stock,StockMinimo");

        List<Producto> listaProductos = productoService.findAll();
        for (Producto p : listaProductos) {
            writer.println(p.getId() + "," +
                    p.getNombre().replace(",", " ") + "," +
                    (p.getDescripcion() != null ? p.getDescripcion().replace(",", " ") : "") + "," +
                    p.getPrecio() + "," +
                    p.getCantidad() + "," +
                    (p.getStockMinimo() != null ? p.getStockMinimo() : 0));
        }
        writer.flush();
        writer.close();
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

package pe.edu.cibertec.inventarioservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.inventarioservice.dto.ProductoRequest;
import pe.edu.cibertec.inventarioservice.dto.MovimientoRequest;
import pe.edu.cibertec.inventarioservice.model.MovimientoInventario;
import pe.edu.cibertec.inventarioservice.model.Producto;
import pe.edu.cibertec.inventarioservice.repository.MovimientoInventarioRepository;
import pe.edu.cibertec.inventarioservice.repository.ProductoRepository;
import java.util.List;

@Service
public class InventarioService {
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    public InventarioService(ProductoRepository productoRepository, MovimientoInventarioRepository movimientoRepository) {
        this.productoRepository = productoRepository; this.movimientoRepository = movimientoRepository;
    }
    public List<Producto> listar() { return productoRepository.findAll(); }
    public Producto obtener(Integer id) { return productoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado")); }
    public Producto crear(ProductoRequest r) {
        Producto p = new Producto(); copiar(r,p); return productoRepository.save(p);
    }
    public Producto actualizar(Integer id, ProductoRequest r) { Producto p=obtener(id); copiar(r,p); return productoRepository.save(p); }
    public void eliminar(Integer id) { Producto p=obtener(id); productoRepository.delete(p); }
    public List<MovimientoInventario> listarMovimientos(Integer productoId) { obtener(productoId); return movimientoRepository.findByProductoId(productoId); }
    @Transactional
    public Producto registrarMovimiento(Integer id, MovimientoRequest r) {
        Producto p=obtener(id); String tipo=r.tipo().trim().toUpperCase();
        if (r.cantidad() == null || r.cantidad() <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        if ("SALIDA".equals(tipo)) { if(p.getCantidad()<r.cantidad()) throw new IllegalArgumentException("Stock insuficiente. Stock actual: "+p.getCantidad()); p.setCantidad(p.getCantidad()-r.cantidad()); }
        else if("ENTRADA".equals(tipo)) p.setCantidad(p.getCantidad()+r.cantidad());
        else throw new IllegalArgumentException("Tipo inválido. Use ENTRADA o SALIDA");
        productoRepository.save(p);
        MovimientoInventario m=new MovimientoInventario(); m.setTipo(tipo); m.setCantidad(r.cantidad()); m.setMotivo(r.motivo()); m.setProductoId(id); m.setUsuarioId(r.usuarioId()); movimientoRepository.save(m);
        return p;
    }
    private void copiar(ProductoRequest r, Producto p) {
        p.setNombre(r.nombre().trim()); p.setDescripcion(r.descripcion()); p.setImagen(r.imagen()==null||r.imagen().isBlank()?"default.jpg":r.imagen());
        p.setPrecio(r.precio()); p.setCantidad(r.cantidad()); p.setStockMinimo(r.stockMinimo()); p.setUsuarioId(r.usuarioId());
    }
}

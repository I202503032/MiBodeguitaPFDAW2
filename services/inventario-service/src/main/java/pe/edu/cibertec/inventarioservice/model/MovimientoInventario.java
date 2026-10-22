package pe.edu.cibertec.inventarioservice.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false) private String tipo;
    @Column(nullable = false) private int cantidad;
    @Column(nullable = false) private String motivo;
    @Temporal(TemporalType.TIMESTAMP) @Column(nullable = false) private Date fecha = new Date();
    @Column(name = "producto_id", nullable = false) private Integer productoId;
    @Column(name = "usuario_id") private Integer usuarioId;
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
}

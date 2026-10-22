package pe.edu.cibertec.inventarioservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.inventarioservice.model.MovimientoInventario;
import java.util.List;
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {
    List<MovimientoInventario> findByProductoId(Integer productoId);
}

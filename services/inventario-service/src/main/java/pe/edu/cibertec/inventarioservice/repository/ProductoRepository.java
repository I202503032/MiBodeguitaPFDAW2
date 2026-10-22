package pe.edu.cibertec.inventarioservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.inventarioservice.model.Producto;
public interface ProductoRepository extends JpaRepository<Producto, Integer> {}

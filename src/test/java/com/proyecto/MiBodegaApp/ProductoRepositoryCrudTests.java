package com.proyecto.MiBodegaApp;

import com.proyecto.MiBodegaApp.model.Producto;
import com.proyecto.MiBodegaApp.repository.IProductoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductoRepositoryCrudTests {
    @Autowired private IProductoRepository productoRepository;

    @Test
    void debeRegistrarListarActualizarYEliminarProducto() {
        Producto producto = new Producto();
        producto.setNombre("Producto de prueba CRUD");
        producto.setDescripcion("Registro para prueba automatizada");
        producto.setImagen("default.jpg");
        producto.setPrecio(4.50);
        producto.setCantidad(10);

        Producto guardado = productoRepository.saveAndFlush(producto);
        assertNotNull(guardado.getId(), "El producto debe recibir un ID al insertarse");
        assertTrue(productoRepository.findAll().stream().anyMatch(p -> p.getId().equals(guardado.getId())), "El producto debe aparecer en el listado");

        guardado.setPrecio(5.25);
        guardado.setCantidad(8);
        Producto actualizado = productoRepository.saveAndFlush(guardado);
        assertEquals(5.25, actualizado.getPrecio(), 0.001);
        assertEquals(8, actualizado.getCantidad());

        productoRepository.deleteById(actualizado.getId());
        productoRepository.flush();
        assertFalse(productoRepository.existsById(actualizado.getId()), "El producto debe eliminarse");
    }
}

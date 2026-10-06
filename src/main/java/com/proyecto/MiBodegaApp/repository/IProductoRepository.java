package com.proyecto.MiBodegaApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.MiBodegaApp.model.Producto;
@Repository
public interface IProductoRepository extends JpaRepository<Producto, Integer>{

}

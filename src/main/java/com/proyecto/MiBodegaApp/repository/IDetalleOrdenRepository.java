package com.proyecto.MiBodegaApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.MiBodegaApp.model.DetalleOrden;

@Repository
public interface IDetalleOrdenRepository  extends JpaRepository<DetalleOrden, Integer>{

}

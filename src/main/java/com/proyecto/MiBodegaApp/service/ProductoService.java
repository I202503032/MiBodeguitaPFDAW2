package com.proyecto.MiBodegaApp.service;

import java.util.List;
import java.util.Optional;

import com.proyecto.MiBodegaApp.model.Producto;

public interface ProductoService {
	public Producto save(Producto producto);
	public Optional<Producto> get (Integer id);
	public void update(Producto producto);
	public void delate(Integer id);
	public List<Producto> findAll();

}

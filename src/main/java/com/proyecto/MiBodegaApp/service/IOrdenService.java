package com.proyecto.MiBodegaApp.service;

import java.util.List;

import com.proyecto.MiBodegaApp.model.Orden;
import com.proyecto.MiBodegaApp.model.Usuario;

public interface IOrdenService {
	List<Orden> findAll();
     Orden save (Orden orden);
     String generarNumeroOrden();
     List<Orden> findByUsuario(Usuario usuario);

}

package com.proyecto.MiBodegaApp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.MiBodegaApp.model.Orden;
import com.proyecto.MiBodegaApp.model.Usuario;

@Repository

public interface IOrdenRepository extends JpaRepository<Orden, Integer> {
    List<Orden> findByUsuario(Usuario usuario);
}

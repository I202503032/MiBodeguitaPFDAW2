package com.proyecto.MiBodegaApp.service;

import java.util.Optional;

import com.proyecto.MiBodegaApp.model.Usuario;

public interface IUsuarioService {
    Optional<Usuario> findById(Integer id);
    Usuario Save (Usuario usuario);
    Optional<Usuario> findByEmail(String email);
}

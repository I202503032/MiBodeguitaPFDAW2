package com.proyecto.MiBodegaApp.api.controller;

import com.proyecto.MiBodegaApp.api.dto.LoginRequest;
import com.proyecto.MiBodegaApp.api.dto.UsuarioResponse;
import com.proyecto.MiBodegaApp.service.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "${app.cors.allowed-origin:http://localhost:4200}")
@RequestMapping("/api/auth")
public class AuthRestController {
    private final IUsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    public AuthRestController(IUsuarioService usuarioService, PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        var usuario = usuarioService.findByEmail(request.getEmail());
        if (usuario.isEmpty() || usuario.get().getPassword() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("mensaje", "Correo o contraseña incorrectos"));
        }
        var cuenta = usuario.get();
        String hash = cuenta.getPassword();
        boolean hashBCrypt = hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$");
        boolean valida = hashBCrypt
            ? passwordEncoder.matches(request.getPassword(), hash)
            : hash.equals(request.getPassword()); // Compatibilidad temporal con cuentas antiguas.
        if (!valida) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("mensaje", "Correo o contraseña incorrectos"));
        }
        if (!hashBCrypt) {
            cuenta.setPassword(passwordEncoder.encode(request.getPassword()));
            usuarioService.Save(cuenta); // Migra la cuenta antigua a BCrypt tras autenticarla.
        }
        return ResponseEntity.ok(new UsuarioResponse(cuenta.getId(), cuenta.getNombre(), cuenta.getEmail(), cuenta.getTipo()));
    }
}

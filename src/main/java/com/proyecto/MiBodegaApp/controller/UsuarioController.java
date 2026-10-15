package com.proyecto.MiBodegaApp.controller;


import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.proyecto.MiBodegaApp.model.Orden;
import com.proyecto.MiBodegaApp.model.Usuario;
import com.proyecto.MiBodegaApp.service.IOrdenService;
import com.proyecto.MiBodegaApp.service.IUsuarioService;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/usuario")
public class UsuarioController {
	
	private final Logger logger= LoggerFactory.getLogger(UsuarioController.class);
	
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordenService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	//usuario y registreo
	@GetMapping("/registro")
	public String create() {
		return "usuario/registro";
	}
	@PostMapping("/save")
	public String save(Usuario usuario) {
		usuario.setTipo("USER");
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		usuarioService.Save(usuario);
		
		return "redirect:/";
	}
	
	@GetMapping("/login")
	public String login() {
		return "usuario/login";
	}
	@PostMapping("/acceder")
	public String acceder(Usuario usuario, HttpSession session) {
		Optional<Usuario> user = usuarioService.findByEmail(usuario.getEmail());
		
		if (user.isPresent() && validarYActualizarPassword(user.get(), usuario.getPassword())) {
		    session.setAttribute("idusuario", user.get().getId());
		    if (user.get().getTipo().equals("ADMIN")) {
		        return "redirect:/administrador";
		    } else {
		        return "redirect:/";
		    }
		}
		
	
		return "redirect:/";  
	}
	
	private boolean validarYActualizarPassword(Usuario cuenta, String passwordIngresada) {
		if (cuenta.getPassword() == null || passwordIngresada == null) return false;
		String hash = cuenta.getPassword();
		boolean hashBCrypt = hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$");
		boolean valida = hashBCrypt ? passwordEncoder.matches(passwordIngresada, hash) : hash.equals(passwordIngresada);
		if (valida && !hashBCrypt) {
			cuenta.setPassword(passwordEncoder.encode(passwordIngresada));
			usuarioService.Save(cuenta);
		}
		return valida;
	}

	@GetMapping("/compras")
	public String obtenerCompras(Model model, HttpSession session) {

	    model.addAttribute("sesion", session.getAttribute("idusuario"));

	    Usuario usuario = usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString())).get();
	    List<Orden> ordenes = ordenService.findByUsuario(usuario);
	    model.addAttribute("ordenes", ordenes);

	    return "usuario/compras";
	}
	}
	
	
	
      

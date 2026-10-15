package com.proyecto.MiBodegaApp.controller;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.proyecto.MiBodegaApp.model.Producto;
import com.proyecto.MiBodegaApp.model.Usuario;
import com.proyecto.MiBodegaApp.service.IUsuarioService;
import com.proyecto.MiBodegaApp.service.ProductoService;
import com.proyecto.MiBodegaApp.service.UploadFileService;


import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/productos")
public class ProductoController {
	private final Logger LOGGER = LoggerFactory.getLogger(ProductoController.class);
	
	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private IUsuarioService  usuarioService;

	@Autowired
	private UploadFileService upload;
	
	@GetMapping("")
	  public String Lista(Model model) {
		model.addAttribute("productos", productoService.findAll());
		  return "productos/Lista";
 }
	@GetMapping("/create")
	public String create(){
		return "productos/create";
	}
	@PostMapping("/save")
	public String save(Producto producto, @RequestParam("img")MultipartFile file, HttpSession session) throws IOException {
		LOGGER.info("Este es el objeto producto{}",producto);
		
		Usuario u= usuarioService.findById(Integer.parseInt(session.getAttribute("idusuario").toString() )).get();
		producto.setUsuario(u);
		
		//imagen
		if(producto.getId()==null) {
			String nombreImagen= upload.saveImage(file);
			producto.setImagen(nombreImagen);
		}else {
		}
			
		  productoService.save(producto);
		return "redirect:/productos";
	}
	
	
	
	@GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
		Producto producto= new Producto();
		Optional<Producto> optionalProducto=productoService.get(id);
		producto=  optionalProducto.get();
		
		LOGGER.info("Producto buscado: {}",producto);
		model.addAttribute("producto", producto);
    	return "productos/edit";
    	
    }
	@PostMapping("/update")
	public String update(Producto producto, @RequestParam("img")MultipartFile file) throws IOException {
		if (file.isEmpty()){
			Producto p= new Producto();
			p=productoService.get(producto.getId()).get();
			producto.setImagen(p.getImagen());
		}else {//cuado se edite tbn la imgen
			Producto p = new Producto();
			p=productoService.get(producto.getId()).get();
			
			// eliminar cuando no sea la imagen por defecto
			if(!p.getImagen().equals("default.jpg")) {
				
			}
			
			String nombreImagen= upload.saveImage(file);
			producto.setImagen(nombreImagen);
		}
		productoService.update(producto);
		return "redirect:/productos";
	}
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
		
		Producto p = new Producto();
		p=productoService.get(id).get();
		
		// eliminar cuando no sea la imagen por defecto
		if(!p.getImagen().equals("default.jpg")) {

		}
		
		productoService.delate(id);
		return "redirect:/productos";
	}
	
	
}
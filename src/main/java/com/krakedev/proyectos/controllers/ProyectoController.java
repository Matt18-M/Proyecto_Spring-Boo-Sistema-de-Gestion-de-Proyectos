package com.krakedev.proyectos.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.services.ProyectoService;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

	private final ProyectoService service;

	public ProyectoController(ProyectoService service) {
		this.service = service;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<?> guardar(@RequestBody Proyecto proyecto) {	try {
			Proyecto nuevo = service.guardar(proyecto);
			return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al guardar proyecto: " + e.getMessage());
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping
	public ResponseEntity<?> listar() {
		try {
			List<Proyecto> proyectos = service.listar();
			return ResponseEntity.ok(proyectos);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al listar proyectos: " + e.getMessage());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> buscar(@PathVariable Integer id) {
		try {
			Proyecto proyecto = service.buscar(id);

			if (proyecto == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proyecto no encontrado");
			}

			return ResponseEntity.ok(proyecto);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al buscar proyecto: " + e.getMessage());
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Proyecto proyecto) {
		try {
			Proyecto actualizado = service.actualizar(id, proyecto);

			if (actualizado == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proyecto no encontrado");
			}

			return ResponseEntity.ok(actualizado);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al actualizar proyecto: " + e.getMessage());
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Integer id) {	try {
			boolean eliminado = service.eliminar(id);

			if (!eliminado) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proyecto no encontrado");
			}

			return ResponseEntity.ok("Proyecto eliminado correctamente");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al eliminar proyecto: " + e.getMessage());
		}
	}
}
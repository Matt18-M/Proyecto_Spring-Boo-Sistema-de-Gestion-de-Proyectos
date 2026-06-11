package com.krakedev.proyectos.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.services.TareaService;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

	private final TareaService service;

	public TareaController(TareaService service) {
		this.service = service;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<?> guardar(@RequestBody Tarea tarea) {	try {
			Tarea nueva = service.guardar(tarea);
			return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al guardar tarea: " + e.getMessage());
		}
	}

	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping
	public ResponseEntity<?> listar() {	try {
			List<Tarea> tareas = service.listar();
			return ResponseEntity.ok(tareas);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al listar tareas: " + e.getMessage());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> buscar(@PathVariable Integer id) {
		try {
			Tarea tarea = service.buscar(id);

			if (tarea == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarea no encontrada");
			}

			return ResponseEntity.ok(tarea);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al buscar tarea: " + e.getMessage());
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Tarea tarea) {
		try {
			Tarea actualizada = service.actualizar(id, tarea);

			if (actualizada == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarea no encontrada");
			}

			return ResponseEntity.ok(actualizada);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al actualizar tarea: " + e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Integer id) {
		try {
			boolean eliminado = service.eliminar(id);

			if (!eliminado) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarea no encontrada");
			}

			return ResponseEntity.ok("Tarea eliminada correctamente");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al eliminar tarea: " + e.getMessage());
		}
	}
}
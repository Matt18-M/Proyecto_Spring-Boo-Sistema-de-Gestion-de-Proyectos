package com.krakedev.proyectos.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.repositories.UsuarioRepository;
import com.krakedev.proyectos.security.JwtUtil;
import com.krakedev.proyectos.services.TokenBlackListService;
import com.krakedev.proyectos.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UsuarioService usuarioService;
	private final UsuarioRepository usuarioRepository;
	private final TokenBlackListService blackListService;

	public AuthController(UsuarioService usuarioService, UsuarioRepository usuarioRepository,
			TokenBlackListService blackListService) {
		this.usuarioService = usuarioService;
		this.usuarioRepository = usuarioRepository;
		this.blackListService = blackListService;
	}

	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {

		try {
			Usuario usuarioNuevo = usuarioService.guardar(usuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioNuevo);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al registrar usuario: " + e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {

		String username = credenciales.get("username");
		String password = credenciales.get("password");

		boolean autenticado = usuarioService.autenticar(username, password);

		if (autenticado) {

			Usuario usuario = usuarioRepository.findByUsername(username).get();

			String token = JwtUtil.generarToken(usuario.getUsername(), usuario.getRol());

			return ResponseEntity.ok(Map.of("token", token));

		} else {

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrecta");
		}
	}

	@GetMapping("/perfil")
	public ResponseEntity<?> verPerfil() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String usuario = auth.getName();
		String rol = auth.getAuthorities().iterator().next().getAuthority();

		return ResponseEntity.ok(Map.of(
				"mensaje", "Bienvenido al sistema protegido por Spring Security",
				"usuario", usuario,
				"rol_detectado", rol,
				"status", "Autenticado exitosamente"));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {

		if (authHeader != null && authHeader.startsWith("Bearer ")) {

			String token = authHeader.substring(7);

			blackListService.invalidarToken(token);

			return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada exitosamente. Token invalidado"));
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionado");
	}
}
package com.krakedev.proyectos.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.repositories.UsuarioRepository;

@Service
public class UsuarioService {

	private final UsuarioRepository repository;

	public UsuarioService(UsuarioRepository repository) {
		this.repository = repository;
	}

	public Usuario guardar(Usuario usuario) {

		String passwordEncriptado = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());

		usuario.setPassword(passwordEncriptado);

		return repository.save(usuario);
	}

	public boolean autenticar(String username, String password) {

		Optional<Usuario> usuarioOpt = repository.findByUsername(username);

		if (usuarioOpt.isPresent()) {

			Usuario usuario = usuarioOpt.get();

			if (BCrypt.checkpw(password, usuario.getPassword())) {
				return true;
			}
		}

		return false;
	}
}
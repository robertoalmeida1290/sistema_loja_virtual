package jdev.sistema.loja.virtual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jdev.sistema.loja.virtual.repository.UsuarioRepository;

@Service
public class PessoaUserService {
	
	
	@Autowired
	private UsuarioRepository usuarioRepository;

}

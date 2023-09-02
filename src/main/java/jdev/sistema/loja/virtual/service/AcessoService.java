package jdev.sistema.loja.virtual.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jdev.sistema.loja.virtual.model.Acesso;
import jdev.sistema.loja.virtual.repository.AcessoRepository;

@Service
public class AcessoService {
	
	@Autowired
	private AcessoRepository acessoRepository;
	
	
	public Acesso save(Acesso acesso) {
		/*aqui pode fazer qualquer tipo de validacao.. antes de salvar,,  */
		return acessoRepository.save(acesso);
	}

}

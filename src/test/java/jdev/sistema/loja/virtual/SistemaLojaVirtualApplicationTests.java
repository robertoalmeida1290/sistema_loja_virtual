package jdev.sistema.loja.virtual;

import javax.persistence.ExcludeDefaultListeners;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jdev.sistema.loja.virtual.model.Acesso;
import jdev.sistema.loja.virtual.repository.AcessoRepository;
import jdev.sistema.loja.virtual.service.AcessoService;


@SpringBootTest(classes = SistemaLojaVirtualApplication.class)
public class SistemaLojaVirtualApplicationTests {

	
	@Autowired
	private AcessoService acessoService;
	
	///@Autowired
	///private AcessoRepository acessoRepository;
	

	@Test
	 public void testCadastroAcesso() {
		
		Acesso acesso = new Acesso();
		acesso.setDescricao("teste de acesso 2");
		
		acessoService.save(acesso);
	
		}
}


/*
package jdev.sistema.loja.virtual;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jdev.sistema.loja.virtual.model.Acesso;
import jdev.sistema.loja.virtual.controller.AcessoController;
import jdev.sistema.loja.virtual.repository.AcessoRepository;
import jdev.sistema.loja.virtual.service.AcessoService;


@SpringBootTest(classes = SistemaLojaVirtualApplicationTests.class)
public class SistemaLojaVirtualApplicationTests {
	
	@Autowired
	private AcessoController acessoController;
	
	@Autowired
	private AcessoService acessoService;
	
	@Autowired
	private AcessoRepository acessoRepository;
		
	@Test
	public void testCadastraAcesso() {
		
		Acesso acesso = new Acesso();
		
		acesso.setDescricao("ROLE_ADMIN");

		acessoRepository.save(acesso);
	
	}

}
*/
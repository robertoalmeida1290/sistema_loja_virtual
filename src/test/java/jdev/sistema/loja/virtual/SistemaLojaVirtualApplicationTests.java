package jdev.sistema.loja.virtual;

import javax.persistence.ExcludeDefaultListeners;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jdev.sistema.loja.virtual.controller.AcessoController;
import jdev.sistema.loja.virtual.model.Acesso;
import jdev.sistema.loja.virtual.repository.AcessoRepository;
import jdev.sistema.loja.virtual.service.AcessoService;


@SpringBootTest(classes = SistemaLojaVirtualApplication.class)
public class SistemaLojaVirtualApplicationTests {

	
	@Autowired
	private AcessoService acessoService;
	
	@Autowired
	private AcessoController acessoController;
	
	///private AcessoRepository acessoRepository;
	
	
	/*esta salvando pelo dois medotodo,,, service e controller*/

	@Test
	 public void testCadastroAcesso() {
		
		Acesso acesso = new Acesso();
		acesso.setDescricao("teste de acesso service 12092023");
		

		acessoService.save(acesso);
		}
	@Test
	 public void testCadastroAcesso2() {
		
		Acesso acesso = new Acesso();
		acesso.setDescricao("teste de acesso controller 12092023 ");
		

		acessoController.salvarAcesso(acesso);
		}
}

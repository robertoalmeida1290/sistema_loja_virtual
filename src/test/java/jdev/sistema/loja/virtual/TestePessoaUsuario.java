package jdev.sistema.loja.virtual;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import jdev.sistema.loja.virtual.model.PessoaJuridica;
import jdev.sistema.loja.virtual.repository.PesssoaRepository;
import jdev.sistema.loja.virtual.service.PessoaUserService;
import junit.framework.TestCase;

@Profile("test")
@SpringBootTest(classes = SistemaLojaVirtualApplication.class)
public class TestePessoaUsuario extends TestCase {
	
	
	@Autowired
	private PessoaUserService pessoaUserService;
	
	@Autowired
	private PesssoaRepository pesssoaRepository;
	
	
	@Test
	public void testCadPessoaFisica() {
		
		PessoaJuridica pessoaJuridica = new PessoaJuridica();
		pessoaJuridica.setCnpj("43254684");
		pessoaJuridica.setNome("Roberto Almeida");
		pessoaJuridica.setEmail("roberto@gmail.com");
		pessoaJuridica.setTelefone("45441115800");
		pessoaJuridica.setInscEstadual("4545565656665");
		pessoaJuridica.setInscMunicipal("5454565656565");
		pessoaJuridica.setNomeFantasia("3454546565665");
		pessoaJuridica.setRazaoSocial("545645646");
		
		pesssoaRepository.save(pessoaJuridica);
		
		/*
		PessoaFisica pessoaFisica = new PessoaFisica();
		
		pessoaFisica.setCpf("0597975788");
		pessoaFisica.setNome("Alex fernando");
		pessoaFisica.setEmail("alex.fernando.egidio@gmail.com");
		pessoaFisica.setTelefone("45999795800");
		pessoaFisica.setEmpresa(pessoaFisica);*/
		
	}

}

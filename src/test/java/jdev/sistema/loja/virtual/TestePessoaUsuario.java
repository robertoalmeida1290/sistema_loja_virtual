package jdev.sistema.loja.virtual;

import java.util.Calendar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import jdev.sistema.loja.virtual.controller.PessoaController;
import jdev.sistema.loja.virtual.enums.TipoEndereco;
import jdev.sistema.loja.virtual.model.Endereco;
import jdev.sistema.loja.virtual.model.PessoaJuridica;
import junit.framework.TestCase;

@Profile("test")
@SpringBootTest(classes = SistemaLojaVirtualApplication.class)
public class TestePessoaUsuario extends TestCase {

	@Autowired
	private PessoaController pessoaController;

	@Test
	public void testCadPessoaFisica() throws ExceptionSistemaJava {

		PessoaJuridica pessoaJuridica = new PessoaJuridica();
		pessoaJuridica.setCnpj("" + Calendar.getInstance().getTimeInMillis());
		pessoaJuridica.setNome("Roberot Almeida54");
		pessoaJuridica.setEmail("roberto33311111@arapora.com");
		pessoaJuridica.setTelefone("4645222545112");
		pessoaJuridica.setInscEstadual("62255456565656665");
		pessoaJuridica.setInscMunicipal("555544565656565");
		pessoaJuridica.setNomeFantasia("5455652265665");
		pessoaJuridica.setRazaoSocial("465226656566");
		
		Endereco endereco1 = new Endereco();
		endereco1.setBairro("Jd 44ias");
		endereco1.setCep("556552265");
		endereco1.setComplemento("Casa cinza");
		endereco1.setEmpresa(pessoaJuridica);
		endereco1.setNumero("389");
		endereco1.setPessoa(pessoaJuridica);
		endereco1.setRuaLogra("Av. são joao sext44o");
		endereco1.setTipoEndereco(TipoEndereco.COBRANCA);
		endereco1.setUf("PR");
		endereco1.setCidade("Curitiba44");
		
		
		Endereco endereco2 = new Endereco();
		endereco2.setBairro("Jd Ma44");
		endereco2.setCep("7827877844");
		endereco2.setComplemento("Andar 4");
		endereco2.setEmpresa(pessoaJuridica);
		endereco2.setNumero("555");
		endereco2.setPessoa(pessoaJuridica);
		endereco2.setRuaLogra("Av. maringá");
		endereco2.setTipoEndereco(TipoEndereco.ENTREGA);
		endereco2.setUf("PR");
		endereco2.setCidade("Curitiba");
		
		pessoaJuridica.getEnderecos().add(endereco2);
		pessoaJuridica.getEnderecos().add(endereco1);

		pessoaJuridica = pessoaController.salvarPj(pessoaJuridica).getBody();
		
		assertEquals(true, pessoaJuridica.getId() > 0 );
		
		for (Endereco endereco : pessoaJuridica.getEnderecos()) {
			assertEquals(true, endereco.getId() > 0);
		}
		
		assertEquals(2, pessoaJuridica.getEnderecos().size());

	}

}

package jdev.sistema.loja.virtual.controller;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.sistema.loja.virtual.ExceptionSistemaJava;
import jdev.sistema.loja.virtual.enums.StatusContaReceber;
import jdev.sistema.loja.virtual.model.ContaReceber;
import jdev.sistema.loja.virtual.model.Endereco;
import jdev.sistema.loja.virtual.model.ItemVendaLoja;
import jdev.sistema.loja.virtual.model.PessoaFisica;
import jdev.sistema.loja.virtual.model.StatusRastreio;
import jdev.sistema.loja.virtual.model.VendaCompraLojaVirtual;
import jdev.sistema.loja.virtual.model.dto.ItemVendaDTO;
import jdev.sistema.loja.virtual.model.dto.VendaCompraLojaVirtualDTO;
import jdev.sistema.loja.virtual.repository.ContaReceberRepository;
import jdev.sistema.loja.virtual.repository.EnderecoRepository;
import jdev.sistema.loja.virtual.repository.NotaFiscalVendaRepository;
import jdev.sistema.loja.virtual.repository.StatusRastreioRepository;
import jdev.sistema.loja.virtual.repository.Vd_Cp_Loja_virt_repository;
import jdev.sistema.loja.virtual.service.ServiceSendEmail;
import jdev.sistema.loja.virtual.service.VendaService;

@RestController
public class Vd_Cp_loja_Virt_Controller {

	@Autowired
	private Vd_Cp_Loja_virt_repository vd_Cp_Loja_virt_repository;

	@Autowired
	private EnderecoRepository enderecoRepository;

	@Autowired
	private PessoaController pessoaController;

	@Autowired
	private NotaFiscalVendaRepository notaFiscalVendaRepository;
	
	@Autowired
	private StatusRastreioRepository statusRastreioRepository;
	
	@Autowired
	private VendaService vendaService; 
	
	@Autowired
	private ContaReceberRepository contaReceberRepository;
	
	@Autowired
	private ServiceSendEmail serviceSendEmail;

	@ResponseBody
	@PostMapping(value = "**/salvarVendaLoja")
	public ResponseEntity<VendaCompraLojaVirtualDTO> salvarVendaLoja(
			@RequestBody @Valid VendaCompraLojaVirtual vendaCompraLojaVirtual) throws ExceptionSistemaJava, UnsupportedEncodingException, MessagingException {

		vendaCompraLojaVirtual.getPessoa().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		PessoaFisica pessoaFisica = pessoaController.salvarPf(vendaCompraLojaVirtual.getPessoa()).getBody();
		vendaCompraLojaVirtual.setPessoa(pessoaFisica);

		vendaCompraLojaVirtual.getEnderecoCobranca().setPessoa(pessoaFisica);
		vendaCompraLojaVirtual.getEnderecoCobranca().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		Endereco enderecoCobranca = enderecoRepository.save(vendaCompraLojaVirtual.getEnderecoCobranca());
		vendaCompraLojaVirtual.setEnderecoCobranca(enderecoCobranca);

		vendaCompraLojaVirtual.getEnderecoEntrega().setPessoa(pessoaFisica);
		vendaCompraLojaVirtual.getEnderecoEntrega().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		Endereco enderecoEntrega = enderecoRepository.save(vendaCompraLojaVirtual.getEnderecoEntrega());
		vendaCompraLojaVirtual.setEnderecoEntrega(enderecoEntrega);

		vendaCompraLojaVirtual.getNotaFiscalVenda().setEmpresa(vendaCompraLojaVirtual.getEmpresa());

		for (int i = 0; i < vendaCompraLojaVirtual.getItemVendaLojas().size(); i++) {
			vendaCompraLojaVirtual.getItemVendaLojas().get(i).setEmpresa(vendaCompraLojaVirtual.getEmpresa());
			vendaCompraLojaVirtual.getItemVendaLojas().get(i).setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
		}

		/* Salva primeiro a venda e todo os dados */
		vendaCompraLojaVirtual = vd_Cp_Loja_virt_repository.saveAndFlush(vendaCompraLojaVirtual);
		
		StatusRastreio statusRastreio = new StatusRastreio();
		statusRastreio.setCentroDistribuicao("Loja Local");
		statusRastreio.setCidade("Local");
		statusRastreio.setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		statusRastreio.setEstado("Local");
		statusRastreio.setStatus("Inicio Compra");
		statusRastreio.setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
		
		statusRastreioRepository.save(statusRastreio);

		/* Associa a venda gravada no banco com a nota fiscal */
		vendaCompraLojaVirtual.getNotaFiscalVenda().setVendaCompraLojaVirtual(vendaCompraLojaVirtual);

		/* Persiste novamente as nota fiscal novamente pra ficar amarrada na venda */
		notaFiscalVendaRepository.saveAndFlush(vendaCompraLojaVirtual.getNotaFiscalVenda());

		VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
		compraLojaVirtualDTO.setValorTotal(vendaCompraLojaVirtual.getValorTotal());
		compraLojaVirtualDTO.setPessoa(vendaCompraLojaVirtual.getPessoa());

		compraLojaVirtualDTO.setEntrega(vendaCompraLojaVirtual.getEnderecoEntrega());
		compraLojaVirtualDTO.setCobranca(vendaCompraLojaVirtual.getEnderecoCobranca());

		compraLojaVirtualDTO.setValorDesc(vendaCompraLojaVirtual.getValorDesconto());
		compraLojaVirtualDTO.setValorFrete(vendaCompraLojaVirtual.getValorFret());
		compraLojaVirtualDTO.setId(vendaCompraLojaVirtual.getId());

		for (ItemVendaLoja item : vendaCompraLojaVirtual.getItemVendaLojas()) {

			ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
			itemVendaDTO.setQuantidade(item.getQuantidade());
			itemVendaDTO.setProduto(item.getProduto());

			compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
		}
		
		
		ContaReceber contaReceber = new ContaReceber();
		contaReceber.setDescricao("Venda da loja virtual nº: " + vendaCompraLojaVirtual.getId());
		contaReceber.setDtPagamento(Calendar.getInstance().getTime());
		contaReceber.setDtVencimento(Calendar.getInstance().getTime());
		contaReceber.setEmpresa(vendaCompraLojaVirtual.getEmpresa());
		contaReceber.setPessoa(vendaCompraLojaVirtual.getPessoa());
		contaReceber.setStatus(StatusContaReceber.QUITADA);
		contaReceber.setValorDesconto(vendaCompraLojaVirtual.getValorDesconto());
		contaReceber.setValorTotal(vendaCompraLojaVirtual.getValorTotal());
		
		contaReceberRepository.saveAndFlush(contaReceber);
		
		/*Emil para o comprador*/
		StringBuilder msgemail = new StringBuilder();
		msgemail.append("Olá, ").append(pessoaFisica.getNome()).append("</br>");
		msgemail.append("Você realizou a compra de nº: ").append(vendaCompraLojaVirtual.getId()).append("</br>");
		msgemail.append("Na loja ").append(vendaCompraLojaVirtual.getEmpresa().getNomeFantasia());
		/*assunto, msg, destino*/
		serviceSendEmail.enviarEmailHtml("Compra Realizada", msgemail.toString(), pessoaFisica.getEmail());
		
		/*Email para o vendedor*/
		msgemail = new StringBuilder();
		msgemail.append("Você realizou uma venda, nº " ).append(vendaCompraLojaVirtual.getId());
		serviceSendEmail.enviarEmailHtml("Venda Realizada", msgemail.toString(), vendaCompraLojaVirtual.getEmpresa().getEmail());

		return new ResponseEntity<VendaCompraLojaVirtualDTO>(compraLojaVirtualDTO, HttpStatus.OK);
	}

	@ResponseBody
	@GetMapping(value = "**/consultaVendaId/{id}")
	public ResponseEntity<VendaCompraLojaVirtualDTO> consultaVendaId(@PathVariable("id") Long idVenda) {

		VendaCompraLojaVirtual compraLojaVirtual = vd_Cp_Loja_virt_repository.findByIdExclusao(idVenda);
		
		if (compraLojaVirtual == null) {
			compraLojaVirtual = new VendaCompraLojaVirtual();
		}

		VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();

		compraLojaVirtualDTO.setValorTotal(compraLojaVirtual.getValorTotal());
		compraLojaVirtualDTO.setPessoa(compraLojaVirtual.getPessoa());

		compraLojaVirtualDTO.setEntrega(compraLojaVirtual.getEnderecoEntrega());
		compraLojaVirtualDTO.setCobranca(compraLojaVirtual.getEnderecoCobranca());

		compraLojaVirtualDTO.setValorDesc(compraLojaVirtual.getValorDesconto());
		compraLojaVirtualDTO.setValorFrete(compraLojaVirtual.getValorFret());
		compraLojaVirtualDTO.setId(compraLojaVirtual.getId());

		for (ItemVendaLoja item : compraLojaVirtual.getItemVendaLojas()) {

			ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
			itemVendaDTO.setQuantidade(item.getQuantidade());
			itemVendaDTO.setProduto(item.getProduto());

			compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
		}

		return new ResponseEntity<VendaCompraLojaVirtualDTO>(compraLojaVirtualDTO, HttpStatus.OK);
	}
	
	@ResponseBody
	@DeleteMapping(value = "**/deleteVendaTotalBanco/{idVenda}")
	public ResponseEntity<String> deleteVendaTotalBanco(@PathVariable(value = "idVenda") Long idVenda) {
		
		vendaService.exclusaoTotalVendaBanco(idVenda);
		
		return new ResponseEntity<String>("Venda excluida com sucesso.",HttpStatus.OK);
		
	}
	
	
	@ResponseBody
	@DeleteMapping(value = "**/deleteVendaTotalBanco2/{idVenda}")
	public ResponseEntity<String> deleteVendaTotalBanco2(@PathVariable(value = "idVenda") Long idVenda) {
		
		vendaService.exclusaoTotalVendaBanco2(idVenda);
		
		return new ResponseEntity<String>("Venda excluida logicamente com sucesso!.",HttpStatus.OK);
		
	}
	
	
	
	@ResponseBody
	@PutMapping(value = "**/ativaRegistroVendaBanco/{idVenda}")
	public ResponseEntity<String> ativaRegistroVendaBanco(@PathVariable(value = "idVenda") Long idVenda) {
		
		vendaService.ativaRegistroVendaBanco(idVenda);
		
		return new ResponseEntity<String>("Venda ativada com sucesso!.",HttpStatus.OK);
		
	}
	
	
	
	@ResponseBody
	@GetMapping(value = "**/consultaVendaDinamicaFaixaData/{data1}/{data2}")
	public ResponseEntity<List<VendaCompraLojaVirtualDTO>> 
	                            consultaVendaDinamicaFaixaData(
	                            		@PathVariable("data1") String data1,
	                            		@PathVariable("data2") String data2) throws ParseException{
		
		List<VendaCompraLojaVirtual> compraLojaVirtual = null;
		
		
		compraLojaVirtual = vendaService.consultaVendaFaixaData(data1, data2);
		
		
		if (compraLojaVirtual == null) {
			compraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}
		
		
		
         List<VendaCompraLojaVirtualDTO> compraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		for (VendaCompraLojaVirtual vcl : compraLojaVirtual) {
			
			VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
	
			compraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());
			compraLojaVirtualDTO.setPessoa(vcl.getPessoa());
	
			compraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());
			compraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());
	
			compraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());
			compraLojaVirtualDTO.setValorFrete(vcl.getValorFret());
			compraLojaVirtualDTO.setId(vcl.getId());

			for (ItemVendaLoja item : vcl.getItemVendaLojas()) {
	
				ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
				itemVendaDTO.setQuantidade(item.getQuantidade());
				itemVendaDTO.setProduto(item.getProduto());
	
				compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
			}
			
			compraLojaVirtualDTOList.add(compraLojaVirtualDTO);
		
		}

		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(compraLojaVirtualDTOList, HttpStatus.OK);
		
	}
	
	
	
	@ResponseBody
	@GetMapping(value = "**/consultaVendaDinamica/{valor}/{tipoconsulta}")
	public ResponseEntity<List<VendaCompraLojaVirtualDTO>> 
						consultaVendaDinamica(@PathVariable("valor") String valor,
								              @PathVariable("tipoconsulta") String tipoconsulta) {

		List<VendaCompraLojaVirtual> compraLojaVirtual = null;
		
		if (tipoconsulta.equalsIgnoreCase("POR_ID_PROD")) {
			
			compraLojaVirtual =   vd_Cp_Loja_virt_repository.vendaPorProduto(Long.parseLong(valor));
			
		}else if (tipoconsulta.equalsIgnoreCase("POR_NOME_PROD")) {
			compraLojaVirtual = vd_Cp_Loja_virt_repository.vendaPorNomeProduto(valor.toUpperCase().trim());
		}
		else if (tipoconsulta.equalsIgnoreCase("POR_NOME_CLIENTE")) {
			compraLojaVirtual = vd_Cp_Loja_virt_repository.vendaPorNomeCliente(valor.toUpperCase().trim());
		}
		else if (tipoconsulta.equalsIgnoreCase("POR_ENDERECO_COBRANCA")) {
			compraLojaVirtual = vd_Cp_Loja_virt_repository.vendaPorEndereCobranca(valor.toUpperCase().trim());
		}
		else if (tipoconsulta.equalsIgnoreCase("POR_ENDERECO_ENTREGA")) {
			compraLojaVirtual = vd_Cp_Loja_virt_repository.vendaPorEnderecoEntrega(valor.toUpperCase().trim());
		}
		
		if (compraLojaVirtual == null) {
			compraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}
		
		List<VendaCompraLojaVirtualDTO> compraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		for (VendaCompraLojaVirtual vcl : compraLojaVirtual) {
			
			VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
	
			compraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());
			compraLojaVirtualDTO.setPessoa(vcl.getPessoa());
	
			compraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());
			compraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());
	
			compraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());
			compraLojaVirtualDTO.setValorFrete(vcl.getValorFret());
			compraLojaVirtualDTO.setId(vcl.getId());

			for (ItemVendaLoja item : vcl.getItemVendaLojas()) {
	
				ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
				itemVendaDTO.setQuantidade(item.getQuantidade());
				itemVendaDTO.setProduto(item.getProduto());
	
				compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
			}
			
			compraLojaVirtualDTOList.add(compraLojaVirtualDTO);
		
		}

		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(compraLojaVirtualDTOList, HttpStatus.OK);
	}
	
	
	@ResponseBody
	@GetMapping(value = "**/vendaPorCliente/{idCliente}")
	public ResponseEntity<List<VendaCompraLojaVirtualDTO>> vendaPorCliente(@PathVariable("idCliente") Long idCliente) {

		List<VendaCompraLojaVirtual> compraLojaVirtual = vd_Cp_Loja_virt_repository.vendaPorCliente(idCliente);
		
		if (compraLojaVirtual == null) {
			compraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}
		
		List<VendaCompraLojaVirtualDTO> compraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		for (VendaCompraLojaVirtual vcl : compraLojaVirtual) {
			
			VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
	
			compraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());
			compraLojaVirtualDTO.setPessoa(vcl.getPessoa());
	
			compraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());
			compraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());
	
			compraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());
			compraLojaVirtualDTO.setValorFrete(vcl.getValorFret());
			compraLojaVirtualDTO.setId(vcl.getId());

			for (ItemVendaLoja item : vcl.getItemVendaLojas()) {
	
				ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
				itemVendaDTO.setQuantidade(item.getQuantidade());
				itemVendaDTO.setProduto(item.getProduto());
	
				compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
			}
			
			compraLojaVirtualDTOList.add(compraLojaVirtualDTO);
		
		}

		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(compraLojaVirtualDTOList, HttpStatus.OK);
	}
	
	
	
	@ResponseBody
	@GetMapping(value = "**/consultaVendaPorProdutoId/{id}")
	public ResponseEntity<List<VendaCompraLojaVirtualDTO>> consultaVendaPorProduto(@PathVariable("id") Long idProd) {

		List<VendaCompraLojaVirtual> compraLojaVirtual = vd_Cp_Loja_virt_repository.vendaPorProduto(idProd);
		
		if (compraLojaVirtual == null) {
			compraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
		}
		
		List<VendaCompraLojaVirtualDTO> compraLojaVirtualDTOList = new ArrayList<VendaCompraLojaVirtualDTO>();
		
		for (VendaCompraLojaVirtual vcl : compraLojaVirtual) {
			
			VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
	
			compraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());
			compraLojaVirtualDTO.setPessoa(vcl.getPessoa());
	
			compraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());
			compraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());
	
			compraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());
			compraLojaVirtualDTO.setValorFrete(vcl.getValorFret());
			compraLojaVirtualDTO.setId(vcl.getId());

			for (ItemVendaLoja item : vcl.getItemVendaLojas()) {
	
				ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
				itemVendaDTO.setQuantidade(item.getQuantidade());
				itemVendaDTO.setProduto(item.getProduto());
	
				compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
			}
			
			compraLojaVirtualDTOList.add(compraLojaVirtualDTO);
		
		}

		return new ResponseEntity<List<VendaCompraLojaVirtualDTO>>(compraLojaVirtualDTOList, HttpStatus.OK);
	}

}

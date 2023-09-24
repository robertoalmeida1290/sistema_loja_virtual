package jdev.sistema.loja.virtual.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jdev.sistema.loja.virtual.model.PessoaFisica;

@Repository
public interface PesssoaFisicaRepository extends CrudRepository<PessoaFisica, Long> {
	
	
	
	

}

package jdev.sistema.loja.virtual.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jdev.sistema.loja.virtual.model.PessoaJuridica;

@Repository
public interface PesssoaRepository extends CrudRepository<PessoaJuridica, Long> {

}

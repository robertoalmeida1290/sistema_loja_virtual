package jdev.sistema.loja.virtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jdev.sistema.loja.virtual.model.FormaPagamento;

@Repository
public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Long> {

	@Query(value = "select f from FormaPagamento f where f.empresa.id = ?1")
	List<FormaPagamento> findAll(Long idEmpresa);

}

package jdev.sistema.loja.virtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jdev.sistema.loja.virtual.model.CupDesc;

@Repository
public interface CupDescontoRepository extends JpaRepository<CupDesc, Long> {
	
	
	@Query(value = "select c from CupDesc c where c.empresa.id = ?1")
	public List<CupDesc> cupDescontoPorEmpresa(Long idEmpresa);

}

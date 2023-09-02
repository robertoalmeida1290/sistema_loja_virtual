package jdev.sistema.loja.virtual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan(basePackages = "jdev.sistema.loja.virtual.model")
@ComponentScan(basePackages = {"jdev.*"})
@EnableJpaRepositories(basePackages = {"jdev.sistema.loja.virtual.repository"})
@EnableTransactionManagement
public class SistemaLojaVirtualApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaLojaVirtualApplication.class, args);
	}

}

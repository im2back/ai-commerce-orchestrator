package io.github.froideexplica.repositories;

import java.util.List;
import java.util.Optional;

import io.github.froideexplica.domain.entities.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {

	public Optional<Product> findByCode(String code) {
		return find("code", code).firstResultOptional();
	}

	public List<Product> findByCodes(List<String> codes) {
		return list("code in ?1", codes);
	}
}
package io.github.froideexplica.dto.outputdata;

import io.github.froideexplica.domain.entities.Product;

import java.math.BigDecimal;


public record ProductDTO(

		Long id,

		String name,

		BigDecimal price,

		String code,

		Integer quantity,
	
		String productUrl

) {
	public ProductDTO(Product p) {
		this(p.getId(),p.getName(),p.getPrice(),p.getCode(),p.getQuantity(),p.getProductUrl());
	}
}

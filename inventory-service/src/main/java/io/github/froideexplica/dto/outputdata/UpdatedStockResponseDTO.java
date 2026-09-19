package io.github.froideexplica.dto.outputdata;

import java.math.BigDecimal;

import io.github.froideexplica.domain.entities.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatedStockResponseDTO(

		@NotBlank
		String name,

		@NotNull
		BigDecimal price,

		@NotBlank
		String code,

		// Quantidade que foi comprada (baixada) nesta operação.
		@NotNull
		Integer purchasedQuantity,

		// Saldo em estoque restante após a baixa.
		@NotNull
		Integer remainingQuantity
		) {
	 public UpdatedStockResponseDTO(Product product, Integer purchasedQuantity) {
		this(
				product.getName(),
				product.getPrice(),
				product.getCode(),
				purchasedQuantity,
				product.getQuantity()
		);
	}

}

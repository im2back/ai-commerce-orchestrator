package io.github.froideexplica.dto.input;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record PurchaseHistoryInDTO(
		@NotBlank
		String document,	
		List<PurchasedProductsDTO> products
		) {

}

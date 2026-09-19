package io.github.froideexplica.dto.inputdata;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchasedItemDTO(
		@NotBlank
		String code,
		
		@NotNull
		@Positive
		Integer quantity
		) {

}

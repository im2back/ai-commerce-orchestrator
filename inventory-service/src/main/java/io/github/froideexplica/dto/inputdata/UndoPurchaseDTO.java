package io.github.froideexplica.dto.inputdata;

import jakarta.validation.constraints.NotNull;

public record UndoPurchaseDTO(	
		@NotNull
		Integer quantity
		) {

}

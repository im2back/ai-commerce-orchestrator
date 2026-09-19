package io.github.froideexplica.dto.output;

import java.math.BigDecimal;

public record PurchasedProductDTO(
		String productName,
		Integer quantity,
		BigDecimal value
		
		) {

}

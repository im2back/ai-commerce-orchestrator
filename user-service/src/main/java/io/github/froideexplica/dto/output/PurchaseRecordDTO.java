package io.github.froideexplica.dto.output;

import io.github.froideexplica.model.Status;

import java.math.BigDecimal;


public record PurchaseRecordDTO(
		Long purchaseId,
		
		String productName,

		BigDecimal productPrice,

		String productCode,

		String purchaseDate,

		Integer quantity,

		Status status

) {
	


}

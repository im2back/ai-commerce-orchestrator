package io.github.froideexplica.dto.output;

import java.math.BigDecimal;
import java.util.List;

public record PurchaseHistoryOutDTO(
		String customerName,
		List<PurchasedProductDTO> purchasedProducts,
		BigDecimal total
		) {

}

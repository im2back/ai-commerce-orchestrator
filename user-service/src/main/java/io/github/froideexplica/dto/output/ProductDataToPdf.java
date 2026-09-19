package io.github.froideexplica.dto.output;

import io.github.froideexplica.model.PurchaseRecord;
import io.github.froideexplica.utils.Util;

import java.math.BigDecimal;


public record ProductDataToPdf(
		String name,
		BigDecimal price,
		Integer quantity,
		String data		
		) {
	
	public ProductDataToPdf(PurchaseRecord p){
		this(p.getProductName(),p.getProductprice(),p.getQuantity(), Util.convertDate(p.getPurchaseDate()));
	}

}

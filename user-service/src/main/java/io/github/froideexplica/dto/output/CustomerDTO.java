package io.github.froideexplica.dto.output;

import io.github.froideexplica.model.Customer;
import io.github.froideexplica.model.PurchaseRecord;
import io.github.froideexplica.utils.Util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record CustomerDTO(
		Long id, 
		String name,
		String document,
		String email,
		String phone, 
		AddressDTO address,
		List<PurchaseRecordDTO> purchaseRecord,
		BigDecimal totalDaConta
		)
		
{
	
	public CustomerDTO (Customer c) {
		    this(c.getId(),c.getName(),c.getDocument(),c.getEmail(),c.getPhone(),
				new AddressDTO(c.getAddress()),
				assembleList(c.getPurchaseRecord()),c.getTotal()
				);
	}

	private static List<PurchaseRecordDTO> assembleList(List<PurchaseRecord> list) {
		List<PurchaseRecordDTO> response = new ArrayList<>();

		for (PurchaseRecord p : list) {
			response.add(new PurchaseRecordDTO(p.getId(),p.getProductName(), p.getProductprice(), p.getProductCode(),
					Util.convertDate(p.getPurchaseDate()), p.getQuantity(), p.getStatus()));
		}
		return response;
	}

}

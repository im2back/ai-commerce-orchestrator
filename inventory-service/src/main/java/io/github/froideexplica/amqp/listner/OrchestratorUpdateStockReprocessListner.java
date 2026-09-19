package io.github.froideexplica.amqp.listner;

import java.util.List;

import io.github.froideexplica.amqp.publish.PublishCustomerReprocessHistory;
import io.github.froideexplica.dto.inputdata.PurchaseRequestDTO;
import io.github.froideexplica.dto.outputdata.PurchaseHistoryDTO;
import io.github.froideexplica.dto.outputdata.UpdatedProducts;
import io.github.froideexplica.dto.outputdata.UpdatedStockResponseDTO;
import io.github.froideexplica.service.ProductService;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class OrchestratorUpdateStockReprocessListner {

	@Inject
	private ProductService productService;

	@Inject
	private PublishCustomerReprocessHistory customerReprocessHistory;

//	@Incoming("stock-update")
//	@Blocking
//	public void receiveMessages(String msg) throws  JsonProcessingException {
//
//		PurchaseRequestDTO purchaseRequestDTO = convert(msg);
//		List<UpdatedStockResponseDTO> response = this.productService.updateQuantityProductsAfterPurchase(purchaseRequestDTO.purchasedItems());
//		PurchaseHistoryDTO purchaseHistoryDTO = assemblePurchaseHistoryDTO(purchaseRequestDTO,response);
//		this.customerReprocessHistory.sendReprocessSaveHistory(purchaseHistoryDTO);
//	}
//
//	private PurchaseRequestDTO convert(String payload) throws  JsonProcessingException {
//		var mapper = new ObjectMapper();
//		 return mapper.readValue(payload, PurchaseRequestDTO.class);
//	}
//
//	private PurchaseHistoryDTO assemblePurchaseHistoryDTO(PurchaseRequestDTO dto, List<UpdatedStockResponseDTO> stockUpdateResponseDTOList) {
//
//		List<UpdatedProducts> products = stockUpdateResponseDTOList.stream()
//			    .map(t -> new UpdatedProducts(t.name(), t.price(), t.code(), t.quantity())).toList();
//
//		return  new PurchaseHistoryDTO(dto.document(), products);
//	}
}

package io.github.froideexplica.amqp.publish;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.froideexplica.dto.outputdata.PurchaseHistoryDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class PublishCustomerReprocessHistory {

	@Inject
	@Channel("reprocess-history-out")
	Emitter<String> emitter;

	@ConfigProperty(name = "reprocess.history.exchange")
	String exchangeName;

	@ConfigProperty(name = "reprocess.history.routing-key")
	String routeKey;

//	private final ObjectMapper mapper = new ObjectMapper();
//
//	private String convert(PurchaseHistoryDTO data) throws JsonProcessingException {
//		return mapper.writeValueAsString(data);
//	}
//
//	public void sendReprocessSaveHistory(PurchaseHistoryDTO data) throws JsonProcessingException {
//		String json = convert(data);
//		emitter.send(json);
//	}
}
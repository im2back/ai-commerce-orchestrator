package io.github.froideexplica.dto.outputdata.toll;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.froideexplica.dto.outputdata.ProductDTO;

public record ProductCreatedSuccessDTO(

        @JsonProperty("resultadoDaOperacao")
        String resultadoDaOperacao,

        @JsonProperty("dadosDoObjetoCriado")
        ProductDTO dadosDoObjetoCriado

) implements ProductOperationResult {

    public ProductCreatedSuccessDTO(ProductDTO productDTO) {
        this("sucesso", productDTO);
    }
}
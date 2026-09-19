package io.github.froideexplica.dto.outputdata.toll;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductOperationErrorDTO(

        @JsonProperty("resultadoDaOperacao")
        String resultadoDaOperacao,

        @JsonProperty("mensagem")
        String mensagem

) implements ProductOperationResult {

    public ProductOperationErrorDTO(String mensagem) {
        this("erro", mensagem);
    }
}
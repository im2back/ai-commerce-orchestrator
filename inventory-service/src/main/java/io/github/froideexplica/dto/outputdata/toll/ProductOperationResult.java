package io.github.froideexplica.dto.outputdata.toll;

public sealed interface ProductOperationResult
        permits ProductCreatedSuccessDTO, ProductOperationErrorDTO {
}
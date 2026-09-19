package io.github.froideexplica.validations.customervalidations;


import io.github.froideexplica.dto.input.RegisterCustomerDTO;

public interface CustomerValidations {

	void valid(RegisterCustomerDTO requestDto);
}

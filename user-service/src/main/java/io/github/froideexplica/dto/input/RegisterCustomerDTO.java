package io.github.froideexplica.dto.input;



import io.github.froideexplica.dto.output.AddressDTO;
import jakarta.validation.constraints.NotBlank;

public record RegisterCustomerDTO(
		
		@NotBlank
		String name,
		
		@NotBlank
		String document,
		
		@NotBlank
		String email,
		
		@NotBlank
		String phone,
			
		AddressDTO address

) {

}

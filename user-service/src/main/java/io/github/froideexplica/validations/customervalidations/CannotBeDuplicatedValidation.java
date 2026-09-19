package io.github.froideexplica.validations.customervalidations;

import java.util.ArrayList;
import java.util.List;

import io.github.froideexplica.dto.input.RegisterCustomerDTO;
import io.github.froideexplica.model.Customer;
import io.github.froideexplica.repository.CustomerRepository;
import io.github.froideexplica.validations.exceptions.CustomerRegisterValidationException;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.inject.Inject;

@ApplicationScoped
public class CannotBeDuplicatedValidation implements CustomerValidations {

	@Inject
	private  CustomerRepository repository;
	
	@Override
	public void valid(RegisterCustomerDTO requestDto) {
		List<String> errorMessages = new ArrayList<>();
		
		
	    List<Customer> customers = consulta(requestDto);

	        if (!customers.isEmpty()) {
	            for (Customer customer : customers) {
	                if (customer.getEmail().equals(requestDto.email())) {
	                	errorMessages.add("Email Cannot Be Duplicated.");
	                }
	                if (customer.getDocument().equals(requestDto.document())) {
	                	errorMessages.add("Document Cannot Be Duplicated.");
	                }
	                if (customer.getPhone().equals(requestDto.phone())) {
	                	errorMessages.add("Phone Cannot Be Duplicated.");
	                }
	                if (!errorMessages.isEmpty()) {
			        throw new CustomerRegisterValidationException(errorMessages);
			    }
	            }
	        }
	    }

	private List<Customer> consulta(RegisterCustomerDTO requestDto) {
		List<Customer> customers = repository.findByEmailOrDocumentOrPhone(
	           requestDto.email(), requestDto.document(), requestDto.phone()
	        );
		return customers;
	}
}

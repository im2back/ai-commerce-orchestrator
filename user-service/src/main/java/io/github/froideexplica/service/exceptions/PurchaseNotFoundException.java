package io.github.froideexplica.service.exceptions;

public class PurchaseNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public PurchaseNotFoundException(String msg) {
	super(msg);
	}
}

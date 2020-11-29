package com.spring.exception;

public class ProductNotFoundException extends RuntimeException {

	public ProductNotFoundException(String productId) {
		super("Requested product " + productId + " is invalid");
	}

	public ProductNotFoundException() {
		super("Requested products are unavailable");
	}
}

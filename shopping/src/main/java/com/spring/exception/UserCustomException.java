package com.spring.exception;

public class UserCustomException extends RuntimeException {

	public UserCustomException(String message) {
		super(message);
	}
}

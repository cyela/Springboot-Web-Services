package com.spring.exception;

public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(String username) {
		super("Username " + username + " is invalid");
	}
}

package com.spring.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spring.model.*;
import com.spring.repository.AddressRepository;
import com.spring.repository.UserRepository;
import com.spring.util.jwtUtil;


@RestController
@RequestMapping("/user")
public class MainController {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private AddressRepository addrRepo;
	
	@Autowired
	private jwtUtil jwtutil;
	
	@PostMapping("/signup")
	public ResponseEntity<String> addUser(@Valid @RequestBody User user) {
		userRepo.save(user);
		return new ResponseEntity<String>(HttpStatus.CREATED);
	}
	
	@PostMapping("/verify")
	public ResponseEntity<Map<String,String>> verifyUser(@Valid @RequestBody Map<String, String> credential) {
		
		String email=credential.get("email");
		String password=credential.get("password");
		User loggedUser=userRepo.findByEmail(email);
		Map<String,String> map=new HashMap<>();
		if(loggedUser.getPassword().equals(password)) {
			String jwtToken=jwtutil.createToken(email, password);
			map.put("status", "200");
			map.put("message", "AUTHORIZED");
			map.put("jToken", jwtToken);
			return new ResponseEntity<Map<String,String>>(map, HttpStatus.ACCEPTED);
		}else {
			
			return new ResponseEntity<Map<String,String>>(HttpStatus.UNAUTHORIZED);
		}
	}
	
}

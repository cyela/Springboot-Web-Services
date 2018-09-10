package com.spring.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spring.model.*;
import com.spring.repository.AddressRepository;
import com.spring.repository.UserRepository;
import com.spring.util.jwtUtil;


@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private AddressRepository addrRepo;
	
	@Autowired
	private jwtUtil jwtutil;
	
	@PostMapping("/signup")
	public ResponseEntity<String> addUser(@Valid @RequestBody User user) {
		try {
		userRepo.save(user);
		return new ResponseEntity<String>(HttpStatus.CREATED);
		}
		catch(Exception e) {
			System.out.println(e.toString());
			return new ResponseEntity<String>(HttpStatus.CONFLICT);
		}
	}
	
	@PostMapping("/verify")
	public ResponseEntity<Map<String,String>> verifyUser(@Valid @RequestBody Map<String, String> credential) {
		
		String email=credential.get("email");
		String password=credential.get("password");
		User loggedUser=userRepo.findByEmailAndPassword(email, password);
		Map<String,String> map=new HashMap<>();
		if(loggedUser!=null) {
			String jwtToken=jwtutil.createToken(email, password);
			map.put("Status", "200");
			map.put("AUTH_TOKEN", jwtToken);
			return new ResponseEntity<Map<String,String>>(map, HttpStatus.ACCEPTED);
		}else {
			
			return new ResponseEntity<Map<String,String>>(HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@PostMapping("/addAddress")
	public ResponseEntity<Map<String,String>> addAddress(@Valid @RequestBody Address address, @RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN ) {
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			User user=jwtutil.checkToken(AUTH_TOKEN);
			user.setAddress(address);
			address.setUser(user);
			addrRepo.save(address);
			return new ResponseEntity<Map<String,String>>(HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<Map<String,String>>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@PostMapping("/getAddress")
	public ResponseEntity<Address> getAddress(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN ) {
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			User user=jwtutil.checkToken(AUTH_TOKEN);
			Address adr=addrRepo.findByUser(user);
			return new ResponseEntity<Address>(adr,HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<Address>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	
	
}

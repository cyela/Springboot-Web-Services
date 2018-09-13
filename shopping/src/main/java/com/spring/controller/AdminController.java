package com.spring.controller;

import java.io.IOException;
import java.sql.Blob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spring.model.*;
import com.spring.repository.AddressRepository;
import com.spring.repository.ProductRepository;
import com.spring.repository.UserRepository;
import com.spring.util.jwtUtil;


@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ProductRepository prodRepo;
	
	@Autowired
	private jwtUtil jwtutil;

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

	@PostMapping("/addProduct")
	public ResponseEntity<String> addProduct(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN,
			@RequestParam(name="file") MultipartFile prodImage,
				@RequestParam(name="description") String description,
					@RequestParam(name="price") String price,
						@RequestParam(name="productname") String productname,
							@RequestParam(name="quantity") String quantity
			) throws IOException {
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			
			Product prod=new Product();
			prod.setDescription(description);
			prod.setPrice(Double.parseDouble(price));
			prod.setProductname(productname);
			prod.setQuantity(Integer.parseInt(quantity));
			prod.setProductimage(prodImage.getBytes());
			prodRepo.save(prod);
			return new ResponseEntity<String>(HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@PostMapping("/getProducts")
	public ResponseEntity<List<Product>> getProducts(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN) throws IOException {
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			List<Product> prodList=prodRepo.findAll();
			
			return new ResponseEntity<List<Product>>(prodList,HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<List<Product>>(HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@PostMapping("/updateProducts")
	public ResponseEntity<String> updateProducts(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN, 
			@RequestParam(name="file") MultipartFile prodImage,
				@RequestParam(name="description") String description,
					@RequestParam(name="price") String price,
						@RequestParam(name="productname") String productname,
							@RequestParam(name="quantity") String quantity,
								@RequestParam(name="productid") String productid) throws IOException {
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			
			Product prod=new Product(Integer.parseInt(productid), 
						description, productname, Double.parseDouble(price),
							Integer.parseInt(quantity), prodImage.getBytes());
			prodRepo.save(prod);
			
			return new ResponseEntity<String>(HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@DeleteMapping("/delProduct")
	public ResponseEntity<String> delProduct(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN, 
			@RequestParam(name="productid") String productid ) throws IOException {
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			
			
			prodRepo.deleteByProductid(Integer.parseInt(productid));
			
			return new ResponseEntity<String>(HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
}

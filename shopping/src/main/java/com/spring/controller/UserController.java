package com.spring.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spring.model.*;
import com.spring.repository.AddressRepository;
import com.spring.repository.CartRepository;
import com.spring.repository.ProductRepository;
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
	private ProductRepository prodRepo;
	
	@Autowired
	private CartRepository cartRepo;
	
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

	@PostMapping("/addToCart")
	public ResponseEntity<List<Product>> addToCart(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN,
			@RequestParam("productId") String productId) throws IOException {
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			User loggedUser=jwtutil.checkToken(AUTH_TOKEN);
			Product cartItem=prodRepo.findByProductid(Integer.parseInt(productId));
			
			Bufcart buf=new Bufcart();
			buf.setEmail(loggedUser.getEmail());
			buf.setQuantity(1);
			buf.setPrice(cartItem.getPrice());
			buf.setProductId(Integer.parseInt(productId));
			Date date=new Date();
			buf.setDateAdded(date);
			cartRepo.save(buf);
			return new ResponseEntity<List<Product>>(HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<List<Product>>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@GetMapping("/viewCart")
	public ResponseEntity<List<Bufcart>> viewCart(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN) throws IOException {
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			User loggedUser=jwtutil.checkToken(AUTH_TOKEN);
			
			List<Bufcart> bufcartlist=cartRepo.findByEmail(loggedUser.getEmail());
			return new ResponseEntity<List<Bufcart>>(bufcartlist,HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<List<Bufcart>>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@PostMapping("/updateCart")
	public ResponseEntity<List<Bufcart>> updateCart(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN,
				@RequestParam(name="bufcartid") String bufcartid,@RequestParam(name="quantity") String quantity) throws IOException {
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			User loggedUser=jwtutil.checkToken(AUTH_TOKEN);
			Bufcart selCart=cartRepo.findByBufcartIdAndEmail(Integer.parseInt(bufcartid), loggedUser.getEmail());
			selCart.setQuantity(Integer.parseInt(quantity));
			cartRepo.save(selCart);
			List<Bufcart> bufcartlist=cartRepo.findByEmail(loggedUser.getEmail());
			return new ResponseEntity<List<Bufcart>>(bufcartlist,HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<List<Bufcart>>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@DeleteMapping("/delCart")
	public ResponseEntity<String> delCart(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN,
				@RequestParam(name="bufcartid") String bufcartid) throws IOException {
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			User loggedUser=jwtutil.checkToken(AUTH_TOKEN);
			cartRepo.deleteByBufcartIdAndEmail(Integer.parseInt(bufcartid), loggedUser.getEmail());
			return new ResponseEntity<String>(HttpStatus.ACCEPTED);
		}
		else {
			return new ResponseEntity<String>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
	// place order
	
	
}

package com.spring.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spring.model.*;
import com.spring.repository.*;
import com.spring.response.*;
import com.spring.util.jwtUtil;


@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ProductRepository prodRepo;
	
	@Autowired
	private OrderRepository ordRepo;
	@Autowired
	private CartRepository cartRepo;
	
	
	@Autowired
	private jwtUtil jwtutil;

	@PostMapping("/verify")
	public ResponseEntity<serverResp> verifyUser(@Valid @RequestBody Map<String, String> credential) {
		
		String email=credential.get("email");
		String password=credential.get("password");
		User loggedUser=userRepo.findByEmailAndPasswordAndUsertype(email, password,"admin");
		serverResp resp=new serverResp();
		if(loggedUser!=null) {
			String jwtToken=jwtutil.createToken(email, password,"admin");
			resp.setStatus("200");
			resp.setMessage("VALID");
			resp.setAUTH_TOKEN(jwtToken);
			return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
		}else {
			resp.setStatus("401");
			resp.setMessage("IN-VALID");
			return new ResponseEntity<serverResp>(resp,HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@PostMapping("/addProduct")
	public ResponseEntity<serverResp> addProduct(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN,
			@RequestParam(name="file") MultipartFile prodImage,
				@RequestParam(name="description") String description,
					@RequestParam(name="price") String price,
						@RequestParam(name="productname") String productname,
							@RequestParam(name="quantity") String quantity
			) throws IOException {
		serverResp resp=new serverResp();
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			try {
				Product prod=new Product();
				prod.setDescription(description);
				prod.setPrice(Double.parseDouble(price));
				prod.setProductname(productname);
				prod.setQuantity(Integer.parseInt(quantity));
				prod.setProductimage(prodImage.getBytes());
				prodRepo.save(prod);
				resp.setStatus("200");
				resp.setMessage("ADD_PRO");
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<serverResp>(resp,HttpStatus.ACCEPTED);
			}catch(Exception e) {
				resp.setStatus("410");
				resp.setMessage(e.toString());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<serverResp>(resp,HttpStatus.ACCEPTED);
			}
		}
			
		else {
			resp.setStatus("401");
			resp.setMessage("IN-VALID");
			return new ResponseEntity<serverResp>(resp,HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@PostMapping("/getProducts")
	public ResponseEntity<prodResp> getProducts(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN) throws IOException {
		prodResp resp=new prodResp();
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			try {
				resp.setStatus("200");
				resp.setMessage("LIST_PRO");
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(prodRepo.findAll());
				return new ResponseEntity<prodResp>(resp,HttpStatus.ACCEPTED);
				}catch(Exception e) {
					resp.setStatus("411");
					resp.setMessage(e.toString());
					resp.setAUTH_TOKEN(AUTH_TOKEN);
					return new ResponseEntity<prodResp>(resp,HttpStatus.ACCEPTED);
				}
		}
		else {
			resp.setStatus("401");
			resp.setMessage("IN-VALID");
			return new ResponseEntity<prodResp>(resp,HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@PostMapping("/updateProducts")
	public ResponseEntity<serverResp> updateProducts(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN, 
			@RequestParam(name="file") MultipartFile prodImage,
				@RequestParam(name="description") String description,
					@RequestParam(name="price") String price,
						@RequestParam(name="productname") String productname,
							@RequestParam(name="quantity") String quantity,
								@RequestParam(name="productid") String productid) throws IOException {
		serverResp resp=new serverResp();
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			try {
			Product prod=new Product(Integer.parseInt(productid), 
						description, productname, Double.parseDouble(price),
							Integer.parseInt(quantity), prodImage.getBytes());
			prodRepo.save(prod);
			resp.setStatus("200");
			resp.setMessage("UPD_PRO");
			resp.setAUTH_TOKEN(AUTH_TOKEN);
			return new ResponseEntity<serverResp>(resp,HttpStatus.ACCEPTED);
			}catch(Exception e) {
				resp.setStatus("412");
				resp.setMessage(e.toString());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<serverResp>(resp,HttpStatus.ACCEPTED);
			}
			
		}
		else {
			resp.setStatus("401");
			resp.setMessage("IN-VALID");
			return new ResponseEntity<serverResp>(resp,HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@PostMapping("/delProduct")
	public ResponseEntity<serverResp> delProduct(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN, 
			@RequestParam(name="productid") String productid ) throws IOException {
		serverResp resp=new serverResp();
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			try {
				prodRepo.deleteByProductid(Integer.parseInt(productid));
				resp.setStatus("200");
				resp.setMessage("DEL_PRO");
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<serverResp>(HttpStatus.ACCEPTED);
			}catch(Exception e) {
				resp.setStatus("413");
				resp.setMessage(e.toString());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<serverResp>(resp,HttpStatus.ACCEPTED);
			}
		}
		else {
			resp.setStatus("401");
			resp.setMessage("IN-VALID");
			return new ResponseEntity<serverResp>(resp,HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@PostMapping("/viewOrders")
	public ResponseEntity<viewOrdResp> viewOrders(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN) throws IOException {
		
		viewOrdResp resp=new viewOrdResp();
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			try {
				resp.setStatus("200");
				resp.setMessage("VW_ORD");
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				List<orderResp> orderList=null;
				for(PlaceOrder pc:ordRepo.findAll()) {
					orderResp ord=new orderResp();
					ord.setOrderId(pc.getOrderId());
					ord.setCartList(cartRepo.findAllByOrderId(pc.getOrderId()));
				}
				return new ResponseEntity<viewOrdResp>(resp,HttpStatus.ACCEPTED);
			}catch(Exception e) {
				resp.setStatus("413");
				resp.setMessage(e.toString());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<viewOrdResp>(resp,HttpStatus.ACCEPTED);
			}
		}
		else {
			resp.setStatus("401");
			resp.setMessage("IN-VALID");
			return new ResponseEntity<viewOrdResp>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
}

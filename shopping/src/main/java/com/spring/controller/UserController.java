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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spring.model.*;
import com.spring.repository.*;
import com.spring.response.*;
import com.spring.util.jwtUtil;

@CrossOrigin(origins = "*")
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
	private OrderRepository ordRepo;
	
	@Autowired
	private jwtUtil jwtutil;
	
	@PostMapping("/signup")
	public ResponseEntity<serverResp> addUser(@Valid @RequestBody User user) {
		
		serverResp resp=new serverResp();
		try {
			resp.setStatus("200");
			resp.setMessage("REGISTERED");
			User reg=userRepo.save(user);
			resp.setObject(reg);
		return new ResponseEntity<serverResp>(resp,HttpStatus.ACCEPTED);
		}
		catch(Exception e) {
			resp.setStatus("400");
			resp.setMessage(e.toString());
			return new ResponseEntity<serverResp>(resp,HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@PostMapping("/verify")
	public ResponseEntity<serverResp> verifyUser(@Valid @RequestBody Map<String, String> credential) {
		
		String email=credential.get("email");
		String password=credential.get("password");
		User loggedUser=userRepo.findByEmailAndPasswordAndUsertype(email, password,"customer");
		serverResp resp=new serverResp();
		if(loggedUser!=null) {
			String jwtToken=jwtutil.createToken(email, password,"customer");
			resp.setStatus("200");
			resp.setMessage("VALID");
			resp.setAUTH_TOKEN(jwtToken);
			return new ResponseEntity<serverResp>(resp, HttpStatus.OK);
		}else {
			resp.setStatus("401");
			resp.setMessage("IN-VALID");
			return new ResponseEntity<serverResp>(resp,HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@PostMapping("/addAddress")
	public ResponseEntity<userResp> addAddress(@Valid @RequestBody Address address, @RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN ) {
		userResp resp=new userResp();
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			try {
				User user=jwtutil.checkToken(AUTH_TOKEN);
				user.setAddress(address);
				address.setUser(user);
				Address adr=addrRepo.saveAndFlush(address);
				resp.setStatus("200");
				resp.setMessage("ADR_UPD");
				resp.setUser(user);
				resp.setAddress(adr);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				
				return new ResponseEntity<userResp>(resp,HttpStatus.ACCEPTED);
			}
			catch(Exception e) {
				resp.setStatus("402");
				resp.setMessage(e.toString());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<userResp>(resp,HttpStatus.ACCEPTED);
			}
		}
		else {
			resp.setStatus("401");
			resp.setMessage("IN-VALID");
			return new ResponseEntity<userResp>(resp,HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@PostMapping("/getAddress")
	public ResponseEntity<response> getAddress(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN ) {
		response resp=new response();
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			try {
				User user=jwtutil.checkToken(AUTH_TOKEN);
				Address adr=addrRepo.findByUser(user);
				HashMap<String,String> map=new HashMap<>();
				map.put("address", adr.getAddress());
				map.put("city", adr.getCity());
				map.put("state", adr.getState());
				map.put("country", adr.getCountry());
				map.put("zipcode", String.valueOf(adr.getZipcode()));
				map.put("phonenumber", adr.getPhonenumber());
				
				resp.setStatus("200");
				resp.setMessage("ADR_UPD");
				resp.setMap(map);
				//resp.setAddress(adr);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				
				return new ResponseEntity<response>(resp,HttpStatus.ACCEPTED);
			}
			catch(Exception e) {
				resp.setStatus("403");
				resp.setMessage(e.toString());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<response>(resp,HttpStatus.ACCEPTED);
			}
		}
		else {
			resp.setStatus("401");
			resp.setMessage("IN-VALID");
			return new ResponseEntity<response>(resp,HttpStatus.NOT_ACCEPTABLE);
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
			}
			catch(Exception e) {
				resp.setStatus("404");
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

	@GetMapping("/addToCart")
	public ResponseEntity<serverResp> addToCart(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN,
			@RequestParam("productId") String productId) throws IOException {
		serverResp resp=new serverResp();
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			try {
				User loggedUser=jwtutil.checkToken(AUTH_TOKEN);
				Product cartItem=prodRepo.findByProductid(Integer.parseInt(productId));
				
				Bufcart buf=new Bufcart();
				buf.setEmail(loggedUser.getEmail());
				buf.setQuantity(1);
				buf.setPrice(cartItem.getPrice());
				buf.setProductId(Integer.parseInt(productId));
				buf.setProductname(cartItem.getProductname());
				Date date=new Date();
				buf.setDateAdded(date);
				cartRepo.save(buf);
				resp.setStatus("200");
				resp.setMessage("CART_UPD");
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<serverResp>(resp,HttpStatus.ACCEPTED);
			}catch(Exception e) {
				resp.setStatus("405");
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
	
	@GetMapping("/viewCart")
	public ResponseEntity<cartResp> viewCart(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN) throws IOException {
		cartResp resp=new cartResp();
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			try {
				User loggedUser=jwtutil.checkToken(AUTH_TOKEN);
				resp.setStatus("200");
				resp.setMessage("LIST_CART");
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(cartRepo.findByEmail(loggedUser.getEmail()));
				return new ResponseEntity<cartResp>(resp,HttpStatus.ACCEPTED);
			}
			catch(Exception e) {
				resp.setStatus("406");
				resp.setMessage(e.toString());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<cartResp>(resp,HttpStatus.ACCEPTED);
			}
		}
		else {
			resp.setStatus("401");
			resp.setMessage("IN-VALID");
			return new ResponseEntity<cartResp>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@GetMapping("/updateCart")
	public ResponseEntity<cartResp> updateCart(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN,
				@RequestParam(name="bufcartid") String bufcartid,@RequestParam(name="quantity") String quantity) throws IOException {
		cartResp resp=new cartResp();
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			try {
				User loggedUser=jwtutil.checkToken(AUTH_TOKEN);
				Bufcart selCart=cartRepo.findByBufcartIdAndEmail(Integer.parseInt(bufcartid), loggedUser.getEmail());
				selCart.setQuantity(Integer.parseInt(quantity));
				cartRepo.save(selCart);
				List<Bufcart> bufcartlist=cartRepo.findByEmail(loggedUser.getEmail());
				resp.setStatus("200");
				resp.setMessage("UPD_CART");
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(bufcartlist);
				return new ResponseEntity<cartResp>(resp,HttpStatus.ACCEPTED);
			}catch(Exception e) {
				resp.setStatus("407");
				resp.setMessage(e.toString());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<cartResp>(resp,HttpStatus.ACCEPTED);
			}

		}
		else {
			resp.setStatus("401");
			resp.setMessage("IN-VALID");
			return new ResponseEntity<cartResp>(resp,HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@GetMapping("/delCart")
	public ResponseEntity<cartResp> delCart(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN,
				@RequestParam(name="bufcartid") String bufcartid) throws IOException {
		cartResp resp=new cartResp();
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			try {
				User loggedUser=jwtutil.checkToken(AUTH_TOKEN);
				cartRepo.deleteByBufcartIdAndEmail(Integer.parseInt(bufcartid), loggedUser.getEmail());
				List<Bufcart> bufcartlist=cartRepo.findByEmail(loggedUser.getEmail());
				resp.setStatus("200");
				resp.setMessage("DEL_CART");
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(bufcartlist);
				return new ResponseEntity<cartResp>(resp,HttpStatus.ACCEPTED);
			}
			catch(Exception e) {
				resp.setStatus("408");
				resp.setMessage(e.toString());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<cartResp>(resp,HttpStatus.ACCEPTED);
			}
		}
		else {
			resp.setStatus("401");
			resp.setMessage("IN-VALID");
			return new ResponseEntity<cartResp>(resp,HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	@GetMapping("/placeOrder")
	public ResponseEntity<serverResp> placeOrder(@RequestHeader(name="AUTH_TOKEN") String AUTH_TOKEN) throws IOException {
		serverResp resp=new serverResp();
		if(jwtutil.checkToken(AUTH_TOKEN)!=null) {
			System.out.println("camed");
			try {
				User loggedUser=jwtutil.checkToken(AUTH_TOKEN);
				PlaceOrder po=new PlaceOrder();
				po.setEmail(loggedUser.getEmail());
				Date date=new Date();
				po.setOrderDate(date);
				po.setOrderStatus("PENDING");
				double total=0;
				List<Bufcart> buflist=cartRepo.findAllByEmail(loggedUser.getEmail());
				for(Bufcart buf:buflist) {
					total=+(buf.getQuantity()*buf.getPrice());
				}
				po.setTotalCost(total);
				PlaceOrder res=ordRepo.save(po);
				System.out.println(res.toString());
				buflist.forEach(bufcart->{
					bufcart.setOrderId(res.getOrderId());
					cartRepo.save(bufcart);
					
				});
				resp.setStatus("200");
				resp.setMessage("PLA_ORD");
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<serverResp>(resp,HttpStatus.ACCEPTED);
			}catch(Exception e) {
				resp.setStatus("409");
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
}

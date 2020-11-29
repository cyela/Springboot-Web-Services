package com.spring.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.constants.ResponseCode;
import com.spring.constants.WebConstants;
import com.spring.exception.UserNotFoundException;
import com.spring.model.Address;
import com.spring.model.Bufcart;
import com.spring.model.PlaceOrder;
import com.spring.model.Product;
import com.spring.model.User;
import com.spring.repository.AddressRepository;
import com.spring.repository.CartRepository;
import com.spring.repository.OrderRepository;
import com.spring.repository.ProductRepository;
import com.spring.repository.UserRepository;
import com.spring.response.cartResp;
import com.spring.response.prodResp;
import com.spring.response.response;
import com.spring.response.serverResp;
import com.spring.response.userResp;
import com.spring.util.Validator;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
public class UserController {

	private static Logger logger = Logger.getLogger(UserController.class.getName());

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

	@PostMapping("/addAddress")
	public ResponseEntity<userResp> addAddress(@RequestBody Address address, Authentication auth) {
		userResp resp = new userResp();
		if (Validator.isAddressEmpty(address)) {
			resp.setStatus(ResponseCode.BAD_REQUEST_CODE);
			resp.setMessage(ResponseCode.BAD_REQUEST_MESSAGE);
		} else {
			try {
				User user = userRepo.findByUsername(auth.getName())
						.orElseThrow(() -> new UsernameNotFoundException(auth.getName()));
				user.setAddress(address);
				address.setUser(user);
				Address adr = addrRepo.saveAndFlush(address);
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.CUST_ADR_ADD);
				resp.setUser(user);
				resp.setAddress(adr);
			} catch (Exception e) {
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.getMessage());
			}
		}
		return new ResponseEntity<userResp>(resp, HttpStatus.ACCEPTED);
	}

	@GetMapping("/getAddress")
	public ResponseEntity<response> getAddress(Authentication auth) {
		response resp = new response();
		try {
			User user = userRepo.findByUsername(auth.getName())
					.orElseThrow(() -> new UsernameNotFoundException(auth.getName()));
			Address adr = addrRepo.findByUser(user);

			HashMap<String, String> map = new HashMap<>();
			map.put(WebConstants.ADR_NAME, adr.getAddress());
			map.put(WebConstants.ADR_CITY, adr.getCity());
			map.put(WebConstants.ADR_STATE, adr.getState());
			map.put(WebConstants.ADR_COUNTRY, adr.getCountry());
			map.put(WebConstants.ADR_ZP, String.valueOf(adr.getZipcode()));
			map.put(WebConstants.PHONE, adr.getPhonenumber());

			resp.setStatus(ResponseCode.SUCCESS_CODE);
			resp.setMessage(ResponseCode.CUST_ADR_ADD);
			resp.setMap(map);
		} catch (Exception e) {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(e.getMessage());
		}
		return new ResponseEntity<response>(resp, HttpStatus.ACCEPTED);
	}

	@GetMapping("/getProducts")
	public ResponseEntity<prodResp> getProducts(Authentication auth) throws IOException {
		prodResp resp = new prodResp();
		try {
			resp.setStatus(ResponseCode.SUCCESS_CODE);
			resp.setMessage(ResponseCode.LIST_SUCCESS_MESSAGE);
			resp.setOblist(prodRepo.findAll());
		} catch (Exception e) {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(e.getMessage());
		}
		return new ResponseEntity<prodResp>(resp, HttpStatus.ACCEPTED);
	}

	@GetMapping("/addToCart")
	public ResponseEntity<serverResp> addToCart(@RequestParam(WebConstants.PROD_ID) String productId,
			Authentication auth) throws IOException {

		serverResp resp = new serverResp();
		try {
			User loggedUser = userRepo.findByUsername(auth.getName())
					.orElseThrow(() -> new UserNotFoundException(auth.getName()));
			Product cartItem = prodRepo.findByProductid(Integer.parseInt(productId));

			Bufcart buf = new Bufcart();
			buf.setEmail(loggedUser.getEmail());
			buf.setQuantity(1);
			buf.setPrice(cartItem.getPrice());
			buf.setProductId(Integer.parseInt(productId));
			buf.setProductname(cartItem.getProductname());
			Date date = new Date();
			buf.setDateAdded(date);

			cartRepo.save(buf);

			resp.setStatus(ResponseCode.SUCCESS_CODE);
			resp.setMessage(ResponseCode.CART_UPD_MESSAGE_CODE);
		} catch (Exception e) {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(e.getMessage());
		}
		return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
	}

	@GetMapping("/viewCart")
	public ResponseEntity<cartResp> viewCart(Authentication auth) throws IOException {
		logger.info("Inside View cart request method");
		cartResp resp = new cartResp();
		try {
			logger.info("Inside View cart request method 2");
			User loggedUser = userRepo.findByUsername(auth.getName())
					.orElseThrow(() -> new UserNotFoundException(auth.getName()));
			resp.setStatus(ResponseCode.SUCCESS_CODE);
			resp.setMessage(ResponseCode.VW_CART_MESSAGE);
			resp.setOblist(cartRepo.findByEmail(loggedUser.getEmail()));
		} catch (Exception e) {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(e.getMessage());
		}

		return new ResponseEntity<cartResp>(resp, HttpStatus.ACCEPTED);
	}

	@PatchMapping("/updateCart")
	public ResponseEntity<cartResp> updateCart(@RequestParam(name = WebConstants.BUF_ID) String bufcartid,
			@RequestParam(name = WebConstants.BUF_QUANTITY) String quantity, Authentication auth) throws IOException {

		cartResp resp = new cartResp();
		try {
			User loggedUser = userRepo.findByUsername(auth.getName())
					.orElseThrow(() -> new UserNotFoundException(auth.getName()));
			Bufcart selCart = cartRepo.findByBufcartIdAndEmail(Integer.parseInt(bufcartid), loggedUser.getEmail());
			selCart.setQuantity(Integer.parseInt(quantity));
			cartRepo.save(selCart);
			List<Bufcart> bufcartlist = cartRepo.findByEmail(loggedUser.getEmail());
			resp.setStatus(ResponseCode.SUCCESS_CODE);
			resp.setMessage(ResponseCode.UPD_CART_MESSAGE);
			resp.setOblist(bufcartlist);
		} catch (Exception e) {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(e.getMessage());
		}

		return new ResponseEntity<cartResp>(resp, HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/delCart")
	public ResponseEntity<cartResp> delCart(@RequestParam(name = WebConstants.BUF_ID) String bufcartid,
			Authentication auth) throws IOException {

		cartResp resp = new cartResp();
		try {
			User loggedUser = userRepo.findByUsername(auth.getName())
					.orElseThrow(() -> new UserNotFoundException(auth.getName()));
			cartRepo.deleteByBufcartIdAndEmail(Integer.parseInt(bufcartid), loggedUser.getEmail());
			List<Bufcart> bufcartlist = cartRepo.findByEmail(loggedUser.getEmail());
			resp.setStatus(ResponseCode.SUCCESS_CODE);
			resp.setMessage(ResponseCode.DEL_CART_SUCCESS_MESSAGE);
			resp.setOblist(bufcartlist);
		} catch (Exception e) {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(e.getMessage());
		}
		return new ResponseEntity<cartResp>(resp, HttpStatus.ACCEPTED);
	}

	@GetMapping("/placeOrder")
	public ResponseEntity<serverResp> placeOrder(Authentication auth) throws IOException {

		serverResp resp = new serverResp();
		try {
			User loggedUser = userRepo.findByUsername(auth.getName())
					.orElseThrow(() -> new UserNotFoundException(auth.getName()));
			PlaceOrder po = new PlaceOrder();
			po.setEmail(loggedUser.getEmail());
			Date date = new Date();
			po.setOrderDate(date);
			po.setOrderStatus(ResponseCode.ORD_STATUS_CODE);
			double total = 0;
			List<Bufcart> buflist = cartRepo.findAllByEmail(loggedUser.getEmail());
			for (Bufcart buf : buflist) {
				total = +(buf.getQuantity() * buf.getPrice());
			}
			po.setTotalCost(total);
			PlaceOrder res = ordRepo.save(po);
			buflist.forEach(bufcart -> {
				bufcart.setOrderId(res.getOrderId());
				cartRepo.save(bufcart);

			});
			resp.setStatus(ResponseCode.SUCCESS_CODE);
			resp.setMessage(ResponseCode.ORD_SUCCESS_MESSAGE);
		} catch (Exception e) {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(e.getMessage());
		}
		return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
	}
}

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

import com.spring.constants.WebConstants;
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

		serverResp resp = new serverResp();
		try {
			resp.setStatus(WebConstants.SUCCESS_CODE);
			resp.setMessage(WebConstants.CUST_REG);
			User reg = userRepo.save(user);
			resp.setObject(reg);
			return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			resp.setStatus(WebConstants.FAILURE_CODE);
			resp.setMessage(e.getMessage());
			return new ResponseEntity<serverResp>(resp, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@PostMapping("/verify")
	public ResponseEntity<serverResp> verifyUser(@Valid @RequestBody Map<String, String> credential) {

		String email = credential.get(WebConstants.USER_EMAIL);
		String password = credential.get(WebConstants.USER_PASSWORD);
		User loggedUser = userRepo.findByEmailAndPasswordAndUsertype(email, password, WebConstants.USER_CUST_ROLE);
		serverResp resp = new serverResp();
		if (loggedUser != null) {
			String jwtToken = jwtutil.createToken(email, password, WebConstants.USER_CUST_ROLE);
			resp.setStatus(WebConstants.SUCCESS_CODE);
			resp.setMessage(WebConstants.SUCCESS_MESSAGE);
			resp.setAUTH_TOKEN(jwtToken);
			return new ResponseEntity<serverResp>(resp, HttpStatus.OK);
		} else {
			resp.setStatus(WebConstants.FAILURE_CODE);
			resp.setMessage(WebConstants.FAILURE_MESSAGE);
			return new ResponseEntity<serverResp>(resp, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@PostMapping("/addAddress")
	public ResponseEntity<userResp> addAddress(@Valid @RequestBody Address address,
			@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN) {
		userResp resp = new userResp();
		if (jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				User user = jwtutil.checkToken(AUTH_TOKEN);
				user.setAddress(address);
				address.setUser(user);
				Address adr = addrRepo.saveAndFlush(address);
				resp.setStatus(WebConstants.SUCCESS_CODE);
				resp.setMessage(WebConstants.CUST_ADR_ADD);
				resp.setUser(user);
				resp.setAddress(adr);
				resp.setAUTH_TOKEN(AUTH_TOKEN);

				return new ResponseEntity<userResp>(resp, HttpStatus.ACCEPTED);
			} catch (Exception e) {
				resp.setStatus(WebConstants.CUST_ADR_FAIL_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<userResp>(resp, HttpStatus.ACCEPTED);
			}
		} else {
			resp.setStatus(WebConstants.FAILURE_CODE);
			resp.setMessage(WebConstants.FAILURE_MESSAGE);
			return new ResponseEntity<userResp>(resp, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@PostMapping("/getAddress")
	public ResponseEntity<response> getAddress(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN) {
		response resp = new response();
		if (jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				User user = jwtutil.checkToken(AUTH_TOKEN);
				Address adr = addrRepo.findByUser(user);
				HashMap<String, String> map = new HashMap<>();
				map.put(WebConstants.ADR_NAME, adr.getAddress());
				map.put(WebConstants.ADR_CITY, adr.getCity());
				map.put(WebConstants.ADR_STATE, adr.getState());
				map.put(WebConstants.ADR_COUNTRY, adr.getCountry());
				map.put(WebConstants.ADR_ZP, String.valueOf(adr.getZipcode()));
				map.put(WebConstants.PHONE, adr.getPhonenumber());

				resp.setStatus(WebConstants.SUCCESS_CODE);
				resp.setMessage(WebConstants.CUST_ADR_ADD);
				resp.setMap(map);
				// resp.setAddress(adr);
				resp.setAUTH_TOKEN(AUTH_TOKEN);

				return new ResponseEntity<response>(resp, HttpStatus.ACCEPTED);
			} catch (Exception e) {
				resp.setStatus(WebConstants.GET_ADR_FAIL_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<response>(resp, HttpStatus.ACCEPTED);
			}
		} else {
			resp.setStatus(WebConstants.FAILURE_CODE);
			resp.setMessage(WebConstants.FAILURE_MESSAGE);
			return new ResponseEntity<response>(resp, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@PostMapping("/getProducts")
	public ResponseEntity<prodResp> getProducts(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN)
			throws IOException {
		prodResp resp = new prodResp();
		if (jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				resp.setStatus(WebConstants.SUCCESS_CODE);
				resp.setMessage(WebConstants.LIST_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(prodRepo.findAll());
				return new ResponseEntity<prodResp>(resp, HttpStatus.ACCEPTED);
			} catch (Exception e) {
				resp.setStatus(WebConstants.LIST_FAILURE_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<prodResp>(resp, HttpStatus.ACCEPTED);
			}
		} else {
			resp.setStatus(WebConstants.FAILURE_CODE);
			resp.setMessage(WebConstants.FAILURE_MESSAGE);
			return new ResponseEntity<prodResp>(resp, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@GetMapping("/addToCart")
	public ResponseEntity<serverResp> addToCart(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
			@RequestParam(WebConstants.PROD_ID) String productId) throws IOException {
		serverResp resp = new serverResp();
		if (jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				User loggedUser = jwtutil.checkToken(AUTH_TOKEN);
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
				resp.setStatus(WebConstants.SUCCESS_CODE);
				resp.setMessage(WebConstants.CART_UPD_MESSAGE_CODE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
			} catch (Exception e) {
				resp.setStatus(WebConstants.CART_UPD_FAIL_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
			}
		} else {
			resp.setStatus(WebConstants.FAILURE_CODE);
			resp.setMessage(WebConstants.FAILURE_MESSAGE);
			return new ResponseEntity<serverResp>(resp, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@GetMapping("/viewCart")
	public ResponseEntity<cartResp> viewCart(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN)
			throws IOException {
		cartResp resp = new cartResp();
		if (jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				User loggedUser = jwtutil.checkToken(AUTH_TOKEN);
				resp.setStatus(WebConstants.SUCCESS_CODE);
				resp.setMessage(WebConstants.VW_CART_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(cartRepo.findByEmail(loggedUser.getEmail()));
				return new ResponseEntity<cartResp>(resp, HttpStatus.ACCEPTED);
			} catch (Exception e) {
				resp.setStatus(WebConstants.VW_CART_FAIL_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<cartResp>(resp, HttpStatus.ACCEPTED);
			}
		} else {
			resp.setStatus(WebConstants.FAILURE_CODE);
			resp.setMessage(WebConstants.FAILURE_MESSAGE);
			return new ResponseEntity<cartResp>(HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@GetMapping("/updateCart")
	public ResponseEntity<cartResp> updateCart(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
			@RequestParam(name = WebConstants.BUF_ID) String bufcartid,
			@RequestParam(name = WebConstants.BUF_QUANTITY) String quantity) throws IOException {
		cartResp resp = new cartResp();
		if (jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				User loggedUser = jwtutil.checkToken(AUTH_TOKEN);
				Bufcart selCart = cartRepo.findByBufcartIdAndEmail(Integer.parseInt(bufcartid), loggedUser.getEmail());
				selCart.setQuantity(Integer.parseInt(quantity));
				cartRepo.save(selCart);
				List<Bufcart> bufcartlist = cartRepo.findByEmail(loggedUser.getEmail());
				resp.setStatus(WebConstants.SUCCESS_CODE);
				resp.setMessage(WebConstants.UPD_CART_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(bufcartlist);
				return new ResponseEntity<cartResp>(resp, HttpStatus.ACCEPTED);
			} catch (Exception e) {
				resp.setStatus(WebConstants.UPD_CART_FAIL_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<cartResp>(resp, HttpStatus.ACCEPTED);
			}

		} else {
			resp.setStatus(WebConstants.FAILURE_CODE);
			resp.setMessage(WebConstants.FAILURE_MESSAGE);
			return new ResponseEntity<cartResp>(resp, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@GetMapping("/delCart")
	public ResponseEntity<cartResp> delCart(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
			@RequestParam(name = WebConstants.BUF_ID) String bufcartid) throws IOException {
		cartResp resp = new cartResp();
		if (jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				User loggedUser = jwtutil.checkToken(AUTH_TOKEN);
				cartRepo.deleteByBufcartIdAndEmail(Integer.parseInt(bufcartid), loggedUser.getEmail());
				List<Bufcart> bufcartlist = cartRepo.findByEmail(loggedUser.getEmail());
				resp.setStatus(WebConstants.SUCCESS_CODE);
				resp.setMessage(WebConstants.DEL_CART_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(bufcartlist);
				return new ResponseEntity<cartResp>(resp, HttpStatus.ACCEPTED);
			} catch (Exception e) {
				resp.setStatus(WebConstants.DEL_CART_FAIL_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<cartResp>(resp, HttpStatus.ACCEPTED);
			}
		} else {
			resp.setStatus(WebConstants.FAILURE_CODE);
			resp.setMessage(WebConstants.FAILURE_MESSAGE);
			return new ResponseEntity<cartResp>(resp, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@GetMapping("/placeOrder")
	public ResponseEntity<serverResp> placeOrder(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN)
			throws IOException {
		serverResp resp = new serverResp();
		if (jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				User loggedUser = jwtutil.checkToken(AUTH_TOKEN);
				PlaceOrder po = new PlaceOrder();
				po.setEmail(loggedUser.getEmail());
				Date date = new Date();
				po.setOrderDate(date);
				po.setOrderStatus(WebConstants.ORD_STATUS_CODE);
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
				resp.setStatus(WebConstants.SUCCESS_CODE);
				resp.setMessage(WebConstants.ORD_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
			} catch (Exception e) {
				resp.setStatus(WebConstants.ORD_FAIL_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
			}
		} else {
			resp.setStatus(WebConstants.FAILURE_CODE);
			resp.setMessage(WebConstants.FAILURE_MESSAGE);
			return new ResponseEntity<serverResp>(resp, HttpStatus.NOT_ACCEPTABLE);
		}
	}
}

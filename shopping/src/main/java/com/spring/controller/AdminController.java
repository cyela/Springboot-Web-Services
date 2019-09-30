package com.spring.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.springframework.web.multipart.MultipartFile;

import com.spring.constants.WebConstants;
import com.spring.model.PlaceOrder;
import com.spring.model.Product;
import com.spring.model.User;
import com.spring.repository.CartRepository;
import com.spring.repository.OrderRepository;
import com.spring.repository.ProductRepository;
import com.spring.repository.UserRepository;
import com.spring.response.order;
import com.spring.response.prodResp;
import com.spring.response.serverResp;
import com.spring.response.viewOrdResp;
import com.spring.util.jwtUtil;

@CrossOrigin(origins = WebConstants.ALLOWED_URL)
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
	public ResponseEntity<serverResp> verifyUser(@Valid @RequestBody HashMap<String, String> credential) {

		String email = credential.get(WebConstants.USER_EMAIL);
		String password = credential.get(WebConstants.USER_PASSWORD);
		User loggedUser = userRepo.findByEmailAndPasswordAndUsertype(email, password, WebConstants.USER_ADMIN_ROLE);
		serverResp resp = new serverResp();
		if (loggedUser != null) {
			String jwtToken = jwtutil.createToken(email, password, WebConstants.USER_ADMIN_ROLE);
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

	@PostMapping("/addProduct")
	public ResponseEntity<prodResp> addProduct(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
			@RequestParam(name = WebConstants.PROD_FILE) MultipartFile prodImage,
			@RequestParam(name = WebConstants.PROD_DESC) String description,
			@RequestParam(name = WebConstants.PROD_PRICE) String price,
			@RequestParam(name = WebConstants.PROD_NAME) String productname,
			@RequestParam(name = WebConstants.PROD_QUANITY) String quantity) throws IOException {
		prodResp resp = new prodResp();
		if (jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				Product prod = new Product();
				prod.setDescription(description);
				prod.setPrice(Double.parseDouble(price));
				prod.setProductname(productname);
				prod.setQuantity(Integer.parseInt(quantity));
				prod.setProductimage(prodImage.getBytes());
				prodRepo.save(prod);
				resp.setStatus(WebConstants.ADD_SUCCESS_CODE);
				resp.setMessage(WebConstants.ADD_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(prodRepo.findAll());
				return new ResponseEntity<prodResp>(resp, HttpStatus.CREATED);
			} catch (Exception e) {
				resp.setStatus(WebConstants.ADD_FAILURE_CODE);
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

	@PostMapping("/updateProducts")
	public ResponseEntity<serverResp> updateProducts(
			@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
			@RequestParam(name = WebConstants.PROD_FILE, required = false) MultipartFile prodImage,
			@RequestParam(name = WebConstants.PROD_DESC) String description,
			@RequestParam(name = WebConstants.PROD_PRICE) String price,
			@RequestParam(name = WebConstants.PROD_NAME) String productname,
			@RequestParam(name = WebConstants.PROD_QUANITY) String quantity,
			@RequestParam(name = WebConstants.PROD_ID) String productid) throws IOException {
		serverResp resp = new serverResp();
		if (jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				Product prodOrg;
				Product prod;
				if (prodImage != null) {
					prod = new Product(Integer.parseInt(productid), description, productname, Double.parseDouble(price),
							Integer.parseInt(quantity), prodImage.getBytes());
				} else {
					prodOrg = prodRepo.findByProductid(Integer.parseInt(productid));
					prod = new Product(Integer.parseInt(productid), description, productname, Double.parseDouble(price),
							Integer.parseInt(quantity), prodOrg.getProductimage());
				}
				prodRepo.save(prod);
				resp.setStatus(WebConstants.SUCCESS_CODE);
				resp.setMessage(WebConstants.UPD_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
			} catch (Exception e) {
				resp.setStatus(WebConstants.UPD_FAILURE_CODE);
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

	@GetMapping("/delProduct")
	public ResponseEntity<prodResp> delProduct(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
			@RequestParam(name = WebConstants.PROD_ID) String productid) throws IOException {
		prodResp resp = new prodResp();
		if (jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				prodRepo.deleteByProductid(Integer.parseInt(productid));
				resp.setStatus(WebConstants.SUCCESS_CODE);
				resp.setMessage(WebConstants.DEL_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				resp.setOblist(prodRepo.findAll());
				return new ResponseEntity<prodResp>(resp, HttpStatus.ACCEPTED);
			} catch (Exception e) {
				resp.setStatus(WebConstants.DEL_FAILURE_CODE);
				resp.setMessage(e.toString());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<prodResp>(resp, HttpStatus.ACCEPTED);
			}
		} else {
			resp.setStatus(WebConstants.FAILURE_CODE);
			resp.setMessage(WebConstants.FAILURE_MESSAGE);
			return new ResponseEntity<prodResp>(resp, HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@GetMapping("/viewOrders")
	public ResponseEntity<viewOrdResp> viewOrders(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN)
			throws IOException {

		viewOrdResp resp = new viewOrdResp();
		if (jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				resp.setStatus(WebConstants.SUCCESS_CODE);
				resp.setMessage(WebConstants.VIEW_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				List<order> orderList = new ArrayList<>();
				// cartRepo;

				order ord;
				List<PlaceOrder> poList = ordRepo.findAll();
				for (PlaceOrder p : poList) {
					ord = new order();
					ord.setOrderBy(p.getEmail());
					ord.setOrderId(p.getOrderId());
					ord.setOrderStatus(p.getOrderStatus());
					ord.setProducts(cartRepo.findAllByOrderId(p.getOrderId()));
					orderList.add(ord);
				}
				resp.setOrderlist(orderList);
				return new ResponseEntity<viewOrdResp>(resp, HttpStatus.ACCEPTED);
			} catch (Exception e) {
				resp.setStatus(WebConstants.VIEW_FAILURE_CODE);
				resp.setMessage(e.getMessage());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<viewOrdResp>(resp, HttpStatus.ACCEPTED);
			}
		} else {
			resp.setStatus(WebConstants.FAILURE_CODE);
			resp.setMessage(WebConstants.FAILURE_MESSAGE);
			return new ResponseEntity<viewOrdResp>(HttpStatus.NOT_ACCEPTABLE);
		}
	}

	@PostMapping("/updateOrder")
	public ResponseEntity<serverResp> updateOrders(
			@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
			@RequestParam(name = WebConstants.ORD_ID) String orderId,
			@RequestParam(name = WebConstants.ORD_STATUS) String orderStatus) throws IOException {

		serverResp resp = new serverResp();
		if (jwtutil.checkToken(AUTH_TOKEN) != null) {
			try {
				PlaceOrder pc = ordRepo.findByOrderId(Integer.parseInt(orderId));
				pc.setOrderStatus(orderStatus);
				ordRepo.save(pc);
				resp.setStatus(WebConstants.SUCCESS_CODE);
				resp.setMessage(WebConstants.UPD_ORD_SUCCESS_MESSAGE);
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
			} catch (Exception e) {
				resp.setStatus(WebConstants.UPD_ORD_FAILURE_CODE);
				resp.setMessage(e.toString());
				resp.setAUTH_TOKEN(AUTH_TOKEN);
				return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
			}
		} else {
			resp.setStatus(WebConstants.FAILURE_CODE);
			resp.setMessage(WebConstants.FAILURE_MESSAGE);
			return new ResponseEntity<serverResp>(HttpStatus.NOT_ACCEPTABLE);
		}
	}
}

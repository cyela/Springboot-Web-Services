package com.spring.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spring.constants.ResponseCode;
import com.spring.constants.WebConstants;
import com.spring.exception.ProductNotFoundException;
import com.spring.model.PlaceOrder;
import com.spring.model.Product;
import com.spring.repository.CartRepository;
import com.spring.repository.OrderRepository;
import com.spring.repository.ProductRepository;
import com.spring.response.order;
import com.spring.response.prodResp;
import com.spring.response.serverResp;
import com.spring.response.viewOrdResp;
import com.spring.util.Validator;

@CrossOrigin(origins = WebConstants.ALLOWED_URL)
@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private ProductRepository prodRepo;

	@Autowired
	private OrderRepository ordRepo;

	@Autowired
	private CartRepository cartRepo;

	@PostMapping("/addProduct")
	public ResponseEntity<prodResp> addProduct(@RequestHeader(name = WebConstants.USER_AUTH_TOKEN) String AUTH_TOKEN,
			@RequestParam(name = WebConstants.PROD_FILE) MultipartFile prodImage,
			@RequestParam(name = WebConstants.PROD_DESC) String description,
			@RequestParam(name = WebConstants.PROD_PRICE) String price,
			@RequestParam(name = WebConstants.PROD_NAME) String productname,
			@RequestParam(name = WebConstants.PROD_QUANITY) String quantity) throws IOException {
		prodResp resp = new prodResp();
		if (Validator.isStringEmpty(productname) || Validator.isStringEmpty(description)
				|| Validator.isStringEmpty(price) || Validator.isStringEmpty(quantity)) {
			resp.setStatus(ResponseCode.BAD_REQUEST_CODE);
			resp.setMessage(ResponseCode.BAD_REQUEST_MESSAGE);
		} else {
			try {
				Product prod = new Product();
				prod.setDescription(description);
				prod.setPrice(Double.parseDouble(price));
				prod.setProductname(productname);
				prod.setQuantity(Integer.parseInt(quantity));
				prod.setProductimage(prodImage.getBytes());
				prodRepo.save(prod);

				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.ADD_SUCCESS_MESSAGE);
				resp.setOblist(prodRepo.findAll());
			} catch (Exception e) {
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.getMessage());
			}
		}
		return new ResponseEntity<prodResp>(resp, HttpStatus.ACCEPTED);
	}

	@GetMapping("/getProducts")
	public ResponseEntity<prodResp> getProducts() throws IOException {
		prodResp resp = new prodResp();

		List<Product> totalListOfProducts = prodRepo.findAll();
		if (totalListOfProducts.isEmpty()) {
			throw new ProductNotFoundException();
		} else {
			resp.setStatus(ResponseCode.SUCCESS_CODE);
			resp.setMessage(ResponseCode.LIST_SUCCESS_MESSAGE);
			resp.setOblist(totalListOfProducts);
		}

		return new ResponseEntity<prodResp>(resp, HttpStatus.ACCEPTED);
	}

	@PutMapping("/updateProducts")
	public ResponseEntity<serverResp> updateProducts(
			@RequestParam(name = WebConstants.PROD_FILE, required = false) MultipartFile prodImage,
			@RequestParam(name = WebConstants.PROD_DESC) String description,
			@RequestParam(name = WebConstants.PROD_PRICE) String price,
			@RequestParam(name = WebConstants.PROD_NAME) String productname,
			@RequestParam(name = WebConstants.PROD_QUANITY) String quantity,
			@RequestParam(name = WebConstants.PROD_ID) String productid) throws IOException {
		serverResp resp = new serverResp();
		if (Validator.isStringEmpty(productname) || Validator.isStringEmpty(description)
				|| Validator.isStringEmpty(price) || Validator.isStringEmpty(quantity)) {
			resp.setStatus(ResponseCode.BAD_REQUEST_CODE);
			resp.setMessage(ResponseCode.BAD_REQUEST_MESSAGE);
		} else {
			try {
				if (prodImage != null) {
					Product prod = new Product(Integer.parseInt(productid), description, productname,
							Double.parseDouble(price), Integer.parseInt(quantity), prodImage.getBytes());
					prodRepo.save(prod);
				} else {
					Product prodOrg = prodRepo.findByProductid(Integer.parseInt(productid));
					Product prod = new Product(Integer.parseInt(productid), description, productname,
							Double.parseDouble(price), Integer.parseInt(quantity), prodOrg.getProductimage());
					prodRepo.save(prod);
				}
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.UPD_SUCCESS_MESSAGE);
			} catch (Exception e) {
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.getMessage());
			}
		}
		return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/delProduct")
	public ResponseEntity<prodResp> delProduct(@RequestParam(name = WebConstants.PROD_ID) String productid)
			throws IOException {
		prodResp resp = new prodResp();
		if (Validator.isStringEmpty(productid)) {
			resp.setStatus(ResponseCode.BAD_REQUEST_CODE);
			resp.setMessage(ResponseCode.BAD_REQUEST_MESSAGE);
		} else {
			try {
				prodRepo.deleteByProductid(Integer.parseInt(productid));
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.DEL_SUCCESS_MESSAGE);
			} catch (Exception e) {
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.toString());
			}
		}
		return new ResponseEntity<prodResp>(resp, HttpStatus.ACCEPTED);
	}

	@GetMapping("/viewOrders")
	public ResponseEntity<viewOrdResp> viewOrders() throws IOException {

		viewOrdResp resp = new viewOrdResp();
		try {
			resp.setStatus(ResponseCode.SUCCESS_CODE);
			resp.setMessage(ResponseCode.VIEW_SUCCESS_MESSAGE);
			List<order> orderList = new ArrayList<>();
			List<PlaceOrder> poList = ordRepo.findAll();
			poList.forEach((po) -> {
				order ord = new order();
				ord.setOrderBy(po.getEmail());
				ord.setOrderId(po.getOrderId());
				ord.setOrderStatus(po.getOrderStatus());
				ord.setProducts(cartRepo.findAllByOrderId(po.getOrderId()));
				orderList.add(ord);
			});
			resp.setOrderlist(orderList);
		} catch (Exception e) {
			resp.setStatus(ResponseCode.FAILURE_CODE);
			resp.setMessage(e.getMessage());
		}

		return new ResponseEntity<viewOrdResp>(resp, HttpStatus.ACCEPTED);
	}

	@PostMapping("/updateOrder")
	public ResponseEntity<serverResp> updateOrders(@RequestParam(name = WebConstants.ORD_ID) String orderId,
			@RequestParam(name = WebConstants.ORD_STATUS) String orderStatus) throws IOException {

		serverResp resp = new serverResp();
		if (Validator.isStringEmpty(orderId) || Validator.isStringEmpty(orderStatus)) {
			resp.setStatus(ResponseCode.BAD_REQUEST_CODE);
			resp.setMessage(ResponseCode.BAD_REQUEST_MESSAGE);
		} else {
			try {
				PlaceOrder pc = ordRepo.findByOrderId(Integer.parseInt(orderId));
				pc.setOrderStatus(orderStatus);
				ordRepo.save(pc);
				resp.setStatus(ResponseCode.SUCCESS_CODE);
				resp.setMessage(ResponseCode.UPD_ORD_SUCCESS_MESSAGE);
			} catch (Exception e) {
				resp.setStatus(ResponseCode.FAILURE_CODE);
				resp.setMessage(e.toString());
			}
		}
		return new ResponseEntity<serverResp>(resp, HttpStatus.ACCEPTED);
	}
}

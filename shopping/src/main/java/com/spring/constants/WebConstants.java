package com.spring.constants;

public class WebConstants {

	public static final String USER_EMAIL = "email";
	public static final String USER_PASSWORD = "password";
	public static final String USER_ADMIN_ROLE = "admin";
	public static final String USER_CUST_ROLE = "customer";

	// Parameters
	public static final String USER_AUTH_TOKEN = "AUTH_TOKEN";
	public static final String PROD_FILE = "file";
	public static final String PROD_DESC = "description";
	public static final String PROD_NAME = "productname";
	public static final String PROD_PRICE = "price";
	public static final String PROD_QUANITY = "quantity";
	public static final String PROD_ID = "productid";

	public static final String ORD_STATUS = "orderStatus";
	public static final String ORD_ID = "orderId";

	// Response
	public static final String SUCCESS_CODE = "200";
	public static final String SUCCESS_MESSAGE = "VALID";

	public static final String FAILURE_CODE = "401";
	public static final String FAILURE_MESSAGE = "IN-VALID";

	public static final String ADD_SUCCESS_CODE = "201";
	public static final String ADD_SUCCESS_MESSAGE = "ADD_PRO";
	public static final String UPD_SUCCESS_MESSAGE = "UPD_PRO";
	public static final String DEL_SUCCESS_MESSAGE = "DEL_PRO";
	public static final String VIEW_SUCCESS_MESSAGE = "VW_ORD";
	public static final String UPD_ORD_SUCCESS_MESSAGE = "UPD_ORD";
	public static final String ADD_FAILURE_CODE = "401";
	public static final String UPD_FAILURE_CODE = "412";
	public static final String DEL_FAILURE_CODE = "413";
	public static final String VIEW_FAILURE_CODE = "414";
	public static final String UPD_ORD_FAILURE_CODE = "415";
	public static final String LIST_SUCCESS_MESSAGE = "LIST_PRO";
	public static final String LIST_FAILURE_CODE = "411";

	// CUSTOMER
	public static final String CUST_REG = "REGISTERED";
	public static final String CUST_ADR_ADD = "ADR_UPD";
	public static final String CUST_ADR_FAIL_CODE = "402";

	// Address
	public static final String ADR_NAME = "address";
	public static final String ADR_CITY = "city";
	public static final String ADR_STATE = "state";
	public static final String ADR_COUNTRY = "country";
	public static final String ADR_ZP = "zipcode";
	public static final String PHONE = "phonenumber";

	public static final String GET_ADR_FAIL_CODE = "403";
	public static final String CART_UPD_MESSAGE_CODE = "CART_UPD";
	public static final String CART_UPD_FAIL_CODE = "405";
	public static final String VW_CART_MESSAGE = "LIST_CART";
	public static final String VW_CART_FAIL_CODE = "406";

	// BUFCART
	public static final String BUF_ID = "bufcartid";
	public static final String BUF_QUANTITY = "quantity";
	public static final String UPD_CART_MESSAGE = "UPD_CART";
	public static final String UPD_CART_FAIL_CODE = "407";

	public static final String DEL_CART_SUCCESS_MESSAGE = "DEL_CART";
	public static final String DEL_CART_FAIL_CODE = "408";

	// Order
	public static final String ORD_STATUS_CODE = "PENDING";
	public static final String ORD_SUCCESS_MESSAGE = "PLA_ORD";
	public static final String ORD_FAIL_CODE = "409";

	// Domains
	public static final String ALLOWED_URL = "http://localhost:4200";
}

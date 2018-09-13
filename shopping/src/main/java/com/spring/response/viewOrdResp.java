package com.spring.response;

import java.util.List;

public class viewOrdResp {
	private String status;
	private String message;
	private String AUTH_TOKEN;
	private List<orderResp> orderList;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getAUTH_TOKEN() {
		return AUTH_TOKEN;
	}
	public void setAUTH_TOKEN(String aUTH_TOKEN) {
		AUTH_TOKEN = aUTH_TOKEN;
	}
	public List<orderResp> getOrderList() {
		return orderList;
	}
	public void setOrderList(List<orderResp> orderList) {
		this.orderList = orderList;
	}
}

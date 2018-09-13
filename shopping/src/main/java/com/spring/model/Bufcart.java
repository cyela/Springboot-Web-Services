package com.spring.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Bufcart implements Serializable{

@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
private int bufcartId;

private String email;

private Date dateAdded;

private int quantity;
private double price;
private int productId;

public int getProductId() {
	return productId;
}


public void setProductId(int productId) {
	this.productId = productId;
}


public int getBufcartId() {
	return bufcartId;
}


public void setBufcartId(int bufcartId) {
	this.bufcartId = bufcartId;
}

public String getEmail() {
	return email;
}


public void setEmail(String email) {
	this.email = email;
}


public Date getDateAdded() {
	return dateAdded;
}


public void setDateAdded(Date dateAdded) {
	this.dateAdded = dateAdded;
}


public int getQuantity() {
	return quantity;
}


public void setQuantity(int quantity) {
	this.quantity = quantity;
}


public double getPrice() {
	return price;
}


public void setPrice(double price) {
	this.price = price;
}


	
	
}

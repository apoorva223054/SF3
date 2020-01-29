package com.nirvanaxp.types.entities;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "order_locking")
@XmlRootElement(name = "order_locking")
public class OrderLocking extends POSNirvanaBaseClassWithBigInt	 implements Serializable
{
	private static final long serialVersionUID = 1L;


	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "session_id")
	private String sessionId;

	@Column(name = "location_id")
	private String locationId;

	@Column(name = "order_id")
	private String orderId;

	@Column(name = "order_number")
	private String orderNumber;


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}



	public String getOrderId() {
		 if(orderId != null && (orderId.length()==0 || orderId.equals("0"))){return null;}else{	return orderId;}
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderNumber() {
		 if(orderNumber != null && (orderNumber.length()==0 || orderNumber.equals("0"))){return null;}else{	return orderNumber;}
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public String toString() {
		return "FutureUpdate [userId=" + userId + ", sessionId=" + sessionId + ", locationId=" + locationId
				+ ", orderId=" + orderId + ", orderNumber=" + orderNumber + "]";
	}

	 
}

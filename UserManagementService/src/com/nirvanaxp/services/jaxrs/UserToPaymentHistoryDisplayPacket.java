package com.nirvanaxp.services.jaxrs;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;


/**
 * @author nirvanaxp
 *
 */
/**
 * @author nirvanaxp
 *
 */
public class UserToPaymentHistoryDisplayPacket {

	private BigDecimal amountPaid;
	private BigDecimal balanceDue;
	private int paymentTypeId;
	private String usersToPaymentId;
	private String orderId;
	private String orderNumber;
	private Timestamp created;
	private Character status;
	private String paymentMethodTypeId;
	public BigDecimal getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(BigDecimal amountPaid) {
		this.amountPaid = amountPaid;
	}

	public BigDecimal getBalanceDue() {
		return balanceDue;
	}
	public void setBalanceDue(BigDecimal balanceDue) {
		this.balanceDue = balanceDue;
	}

	public int getPaymentTypeId() {
		return paymentTypeId;
	}
	public void setPaymentTypeId(int paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public String getUsersToPaymentId() {
		if(usersToPaymentId != null && (usersToPaymentId.length()==0 || usersToPaymentId.equals("0"))){return null;}else{	return usersToPaymentId;}
	}
	public void setUsersToPaymentId(String usersToPaymentId) {
		this.usersToPaymentId = usersToPaymentId;
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

	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public Character getStatus() {
		return status;
	}
	public void setStatus(Character status) {
		this.status = status;
	}
	

	public String getPaymentMethodTypeId() {
		return paymentMethodTypeId;
	}

	public void setPaymentMethodTypeId(String paymentMethodTypeId) {
		this.paymentMethodTypeId = paymentMethodTypeId;
	}
	@Override
	public String toString() {
		return "UserToPaymentHistoryDisplayPacket [amountPaid=" + amountPaid
				+ ", balanceDue=" + balanceDue + ", paymentTypeId="
				+ paymentTypeId + ", usersToPaymentId=" + usersToPaymentId
				+ ", orderId=" + orderId + ", orderNumber=" + orderNumber
				+ ", created=" + created + ", status=" + status
				+ ", paymentMethodTypeId=" + paymentMethodTypeId + "]";
	}
	
}

package com.nirvanaxp.types.entities.user;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;




/**
 * The persistent class for the users_to_payment_history database table.
 * 
 */
@Entity
@Table(name="users_to_payment_history")
@XmlRootElement(name = "users_to_payment_history")
public class UsersToPaymentHistory  extends POSNirvanaBaseClass
{

	private static final long serialVersionUID = 1L;

	@Column(name="amount_paid")
	private BigDecimal amountPaid;
	
	@Column(name="balance_due")
	private BigDecimal balanceDue;

	
	@Column(name="order_payment_details_id")
	private String orderPaymentDetailsId;

	@Column(name="payment_type_id")
	private int paymentTypeId;

	@Column(name="location_id")
	private String locationId;
	
	@Column(name="users_to_payment_id")
	private String  usersToPaymentId;
	
	
	private transient String userId;
	
	@Column(name = "local_time")
	private String localTime;

	@Column(name="payment_method_type_id")
	private String paymentMethodTypeId;


	public String getPaymentMethodTypeId() {
		return paymentMethodTypeId;
	}

	public void setPaymentMethodTypeId(String paymentMethodTypeId) {
		this.paymentMethodTypeId = paymentMethodTypeId;
	}
	
	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}


	public UsersToPaymentHistory() {
	}
	
	public String getOrderPaymentDetailsId() {
		 if(orderPaymentDetailsId != null && (orderPaymentDetailsId.length()==0 || orderPaymentDetailsId.equals("0"))){return null;}else{	return orderPaymentDetailsId;}
	}

	public void setOrderPaymentDetailsId(String orderPaymentDetailsId) {
		this.orderPaymentDetailsId = orderPaymentDetailsId;
	}

	public int getPaymentTypeId() {
		return this.paymentTypeId;
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

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

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

	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "UsersToPaymentHistory [amountPaid=" + amountPaid
				+ ", balanceDue=" + balanceDue + ", orderPaymentDetailsId="
				+ orderPaymentDetailsId + ", paymentTypeId=" + paymentTypeId
				+ ", locationId=" + locationId + ", usersToPaymentId="
				+ usersToPaymentId + ", localTime=" + localTime
				+ ", paymentMethodTypeId=" + paymentMethodTypeId + "]";
	}
	
	
	
}
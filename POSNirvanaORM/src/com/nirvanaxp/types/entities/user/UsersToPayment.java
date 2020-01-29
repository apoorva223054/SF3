package com.nirvanaxp.types.entities.user;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;


/**
 * The persistent class for the users_to_payment database table.
 * 
 */

@Entity
@Table(name="users_to_payment")
@XmlRootElement(name = "users_to_payment")
public class UsersToPayment extends POSNirvanaBaseClassWithoutGeneratedIds
{
	private static final long serialVersionUID = 1L;

	private BigDecimal amount;

	@Column(name="payment_type_id")
	private int paymentTypeId;

	@Column(name="users_id")
	private String usersId;

	
	@Column(name="payment_method_type_id")
	private String paymentMethodTypeId;
	
	/*@Column(name = "local_time")
	private String localTime;

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}*/
	
	public String getPaymentMethodTypeId() {
		return paymentMethodTypeId;
	}

	public void setPaymentMethodTypeId(String paymentMethodTypeId) {
		this.paymentMethodTypeId = paymentMethodTypeId;
	}

	
	public UsersToPayment() {
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public int getPaymentTypeId() {
		return this.paymentTypeId;
	}

	public void setPaymentTypeId(int paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	@Override
	public String toString() {
		return "UsersToPayment [amount=" + amount + ", paymentTypeId="
				+ paymentTypeId + ", usersId=" + usersId
				+ ", paymentMethodTypeId=" + paymentMethodTypeId + "]";
	}

	
	
	

}
/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.payment;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.global.types.entities.accounts.ServicePlan;
import com.nirvanaxp.payment.gateway.data.CreditCard;
import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.user.User;

@XmlRootElement(name = "PaymentPacket")
public class PaymentPacket
{

	private String version = "1.0";

	// which gateway to use for processing payment
	private String paymentGateway;

	// who is paying for this, local user, must have global user id in it
	private User orderUser;

	// credit card data
	private CreditCard monetaryData;

	// nonce when processing thru braintree
	private String braintreeNonce;

	// Either one of the following is passed in

	// this is sent when paying for service plan
	private ServicePlan servicePlan;

	// this is sent when paying for customer order
	private OrderHeader orderHeader;

	public ServicePlan getServicePlan()
	{
		return servicePlan;
	}

	public void setServicePlan(ServicePlan servicePlan)
	{
		this.servicePlan = servicePlan;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getPaymentGateway()
	{
		return paymentGateway;
	}

	public void setPaymentGateway(String paymentGateway)
	{
		this.paymentGateway = paymentGateway;
	}

	public OrderHeader getOrderHeader()
	{
		return orderHeader;
	}

	public void setOrderHeader(OrderHeader orderHeader)
	{
		this.orderHeader = orderHeader;
	}

	public User getOrderUser()
	{
		return orderUser;
	}

	public void setOrderUser(User orderUser)
	{
		this.orderUser = orderUser;
	}

	public CreditCard getMonetaryData()
	{
		return monetaryData;
	}

	public void setMonetaryData(CreditCard monetaryData)
	{
		this.monetaryData = monetaryData;
	}

	public String getBraintreeNonce()
	{
		return braintreeNonce;
	}

	public void setBraintreeNonce(String braintreeNonce)
	{
		this.braintreeNonce = braintreeNonce;
	}

	@Override
	public String toString()
	{
		return "PaymentPacket [version=" + version + ", paymentGateway=" + paymentGateway + ", orderUser=" + orderUser + ", monetaryData=" + monetaryData + ", braintreeNonce=" + braintreeNonce
				+ ", servicePlan=" + servicePlan + ", orderHeader=" + orderHeader + "]";
	}

}

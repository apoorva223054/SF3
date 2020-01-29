/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.orders;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.payment.PaymentGatewayToPinpad;


/**
 * The persistent class for the order_source_to_paymentgateway_type database
 * table.
 * 
 */
@Entity
@Table(name = "order_source_to_paymentgateway_type")
@XmlRootElement(name = "order_source_to_paymentgateway_type")
public class OrderSourceToPaymentgatewayType implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "merchant_id")
	private String merchantId;

	@Column(name = "order_source_id")
	private String orderSourceId;

	private String status;

	private String parameter1;

	private String parameter2;

	private String parameter3;

	private String parameter4;

	private String parameter5;

	private String password;

	@Column(name = "paymentgateway_type_id")
	private int paymentgatewayTypeId;

	private transient String orderSourceName;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;
	
	private transient List<PaymentGatewayToPinpad> pinpadList;

	public OrderSourceToPaymentgatewayType()
	{
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getMerchantId()
	{
		return this.merchantId;
	}

	public void setMerchantId(String merchantId)
	{
		this.merchantId = merchantId;
	}

	public String getOrderSourceId()
	{
		 if(orderSourceId != null && (orderSourceId.length()==0 || orderSourceId.equals("0"))){return null;}else{	return orderSourceId;}
	}

	public void setOrderSourceId(String orderSourceId)
	{
		this.orderSourceId = orderSourceId;
	}

	public String getParameter1()
	{
		return this.parameter1;
	}

	public void setParameter1(String parameter1)
	{
		this.parameter1 = parameter1;
	}

	public String getParameter2()
	{
		return this.parameter2;
	}

	public void setParameter2(String parameter2)
	{
		this.parameter2 = parameter2;
	}

	public String getParameter3()
	{
		return this.parameter3;
	}

	public void setParameter3(String parameter3)
	{
		this.parameter3 = parameter3;
	}

	public String getParameter4()
	{
		return this.parameter4;
	}

	public void setParameter4(String parameter4)
	{
		this.parameter4 = parameter4;
	}

	public String getParameter5()
	{
		return this.parameter5;
	}

	public void setParameter5(String parameter5)
	{
		this.parameter5 = parameter5;
	}

	public String getPassword()
	{
		return this.password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public int getPaymentgatewayTypeId()
	{
		return this.paymentgatewayTypeId;
	}

	public void setPaymentgatewayTypeId(int paymentgatewayTypeId)
	{
		this.paymentgatewayTypeId = paymentgatewayTypeId;
	}


	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getOrderSourceName()
	{
		return orderSourceName;
	}

	public void setOrderSourceName(String orderSourceName)
	{
		this.orderSourceName = orderSourceName;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

	
	public List<PaymentGatewayToPinpad> getPinpadList()
	{
		return pinpadList;
	}

	public void setPinpadList(List<PaymentGatewayToPinpad> pinpadList)
	{
		this.pinpadList = pinpadList;
	}

	@Override
	public String toString() {
		return "OrderSourceToPaymentgatewayType [id=" + id + ", created="
				+ created + ", createdBy=" + createdBy + ", merchantId="
				+ merchantId + ", orderSourceId=" + orderSourceId + ", status="
				+ status + ", parameter1=" + parameter1 + ", parameter2="
				+ parameter2 + ", parameter3=" + parameter3 + ", parameter4="
				+ parameter4 + ", parameter5=" + parameter5 + ", password="
				+ password + ", paymentgatewayTypeId=" + paymentgatewayTypeId
				+ ", orderSourceName=" + orderSourceName + ", updated="
				+ updated + ", updatedBy=" + updatedBy + "]";
	}

}
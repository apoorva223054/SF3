/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.orders;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The persistent class for the address database table.
 * 
 */
@Entity
@Table(name = "nirvana_index")
@XmlRootElement(name = "nirvana_index")
public class NirvanaIndex implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "order_number")
	private String orderNumber;

	@Column(name = "paid_in_out_number")
	private int paidInOutNumber;

	@Column(name = "business_id")
	private int businessId;
	
	 

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrderNumber() {
		 if(orderNumber != null && (orderNumber.length()==0 || orderNumber.equals("0"))){return null;}else{	return orderNumber;}
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public int getBusinessId() {
		return businessId;
	}

	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}

	public int getPaidInOutNumber() {
		return paidInOutNumber;
	}

	public void setPaidInOutNumber(int paidInOutNumber) {
		this.paidInOutNumber = paidInOutNumber;
	}

	@Override
	public String toString() {
		return "NirvanaIndex [id=" + id + ", orderNumber=" + orderNumber
				+ ", paidInOutNumber=" + paidInOutNumber + ", businessId="
				+ businessId + "]";
	}

	 
	
	
}

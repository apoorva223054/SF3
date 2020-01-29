/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.payment;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

@Entity
@Table(name = "order_source_group_to_pinpad")
@XmlRootElement(name = "OrderSourceGroupToPinPad.java")
public class OrderSourceGroupToPinPad extends POSNirvanaBaseClass implements Serializable
{
	@Column(name = "order_source_group_id")
	private String orderSourceGroupId;

	@Column(name = "pinpad_id")
	private String pinPadId;

 

	public String getPinPadId() {
		return pinPadId;
	}

	public void setPinPadId(String pinPadId) {
		this.pinPadId = pinPadId;
	}

	public String getOrderSourceGroupId() {
		 if(orderSourceGroupId != null && (orderSourceGroupId.length()==0 || orderSourceGroupId.equals("0"))){return null;}else{	return orderSourceGroupId;}
	}

	public void setOrderSourceGroupId(String orderSourceGroupId) {
		this.orderSourceGroupId = orderSourceGroupId;
	}

	@Override
	public String toString() {
		return "OrderSourceGroupToPinPad [orderSourceGroupId=" + orderSourceGroupId + ", pinPadId=" + pinPadId + "]";
	}

	 
}

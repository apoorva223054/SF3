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
@Table(name = "order_source_to_pinpad")
@XmlRootElement(name = "OrderSourceToPinPad.java")
public class OrderSourceToPinPad extends POSNirvanaBaseClass implements Serializable
{

 
	
	@Column(name = "order_source_id")
	private int orderSourceId;

	@Column(name = "pinpad_id")
	private String pinPadId;

	public int getOrderSourceId() {
		return orderSourceId;
	}

	public void setOrderSourceId(int orderSourceId) {
		this.orderSourceId = orderSourceId;
	}

	public String getPinPadId() {
		return pinPadId;
	}

	public void setPinPadId(String pinPadId) {
		this.pinPadId = pinPadId;
	}

	@Override
	public String toString() {
		return "OrderSourceToPinPad [orderSourceId=" + orderSourceId + ", pinPadId=" + pinPadId + "]";
	}
	
}

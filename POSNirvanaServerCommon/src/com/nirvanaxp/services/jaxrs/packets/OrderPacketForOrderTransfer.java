/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderHeader;

@XmlRootElement(name = "OrderPacketForOrderTransfer")
public class OrderPacketForOrderTransfer extends PostPacket
{

	private OrderHeader fromOrderHeader;
	private OrderHeader toOrderHeader;
	/*private int fromOrderHeaderId;
	
	private int toOrderHeaderId;*/
	
	private String updatedBy;
	private String rootLocationId;
	
	public OrderHeader getFromOrderHeader() {
		return fromOrderHeader;
	}
	public void setFromOrderHeader(OrderHeader fromOrderHeader) {
		this.fromOrderHeader = fromOrderHeader;
	}
	public OrderHeader getToOrderHeader() {
		return toOrderHeader;
	}
	public void setToOrderHeader(OrderHeader toOrderHeader) {
		this.toOrderHeader = toOrderHeader;
	}
	
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getRootLocationId() {
		return rootLocationId;
	}
	public void setRootLocationId(String rootLocationId) {
		this.rootLocationId = rootLocationId;
	}
	 
	
	/*public int getToOrderHeaderId() {
		return toOrderHeaderId;
	}
	public void setToOrderHeaderId(int toOrderHeaderId) {
		this.toOrderHeaderId = toOrderHeaderId;
	}
	
	public int getFromOrderHeaderId() {
		return fromOrderHeaderId;
	}
	public void setFromOrderHeaderId(int fromOrderHeaderId) {
		this.fromOrderHeaderId = fromOrderHeaderId;
	}*/
	

	
}

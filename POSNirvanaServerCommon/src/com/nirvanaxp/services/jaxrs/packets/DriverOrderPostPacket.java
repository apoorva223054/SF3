package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DriverOrderPostPacket")
public class DriverOrderPostPacket extends PostPacket {
	
	String driverId;
	String orderId;
	String orderStatusId;

	 

	public String getDriverId()
	{
		 if(driverId != null && (driverId.length()==0 || driverId.equals("0"))){return null;}else{	return driverId;}
	}
	public void setDriverId(String driverId)
	{
		this.driverId = driverId;
	}
	public String getOrderId() {
		 if(orderId != null && (orderId.length()==0 || orderId.equals("0"))){return null;}else{	return orderId;}
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderStatusId()
	{
		if(orderStatusId != null && (orderStatusId.length()==0 || orderStatusId.equals("0"))){return null;}else{	return orderStatusId;}
	}
	public void setOrderStatusId(String orderStatusId)
	{
		this.orderStatusId = orderStatusId;
	}

	String updatedBy;

	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}


}

package com.nirvanaxp.services.jaxrs.packets;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.inventory.RequestOrder;

@XmlRootElement(name = "RequestOrderPacket")
public class RequestOrderPacket extends PostPacket
{

	private RequestOrder requestOrder;
	
	private ArrayList<RequestOrder> requestOrders;

	private String supplierRefNo;
	
	public RequestOrder getRequestOrder() {
		return requestOrder;
	}

	public void setRequestOrder(RequestOrder requestOrder) {
		this.requestOrder = requestOrder;
	}

	
	public ArrayList<RequestOrder> getRequestOrders() {
		return requestOrders;
	}

	public void setRequestOrders(ArrayList<RequestOrder> requestOrders) {
		this.requestOrders = requestOrders;
	}

	public String getSupplierRefNo() {
		return supplierRefNo;
	}

	public void setSupplierRefNo(String supplierRefNo) {
		this.supplierRefNo = supplierRefNo;
	}

	@Override
	public String toString() {
		return "RequestOrderPacket [requestOrder=" + requestOrder
				+ ", requestOrders=" + requestOrders + ", supplierRefNo="
				+ supplierRefNo + "]";
	}
	
	
	
	

}

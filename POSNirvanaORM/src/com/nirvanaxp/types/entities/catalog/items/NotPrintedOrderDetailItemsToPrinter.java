/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.catalog.items;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.protocol.RelationalEntities;

/**
 * The persistent class for the items_to_printers database table.
 * 
 */
@Entity
@Table(name = "not_printed_order_detail_items_to_printer")
@XmlRootElement(name = "not_printed_order_detail_items_to_printer")
public class NotPrintedOrderDetailItemsToPrinter extends POSNirvanaBaseClass implements RelationalEntities
{
	private static final long serialVersionUID = 1L;

	@Column(name = "order_detail_items_id")
	private String orderDetailItemsId;

	@Column(name = "printers_id")
	private String printersId;
	
	@Column(name = "order_header_id")
	private String orderHeaderId;
	
	@Column(name = "local_time")
	private String localTime;

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}

	public NotPrintedOrderDetailItemsToPrinter()
	{
	}


	public String getOrderDetailItemsId() {
		 if(orderDetailItemsId != null && (orderDetailItemsId.length()==0 || orderDetailItemsId.equals("0"))){return null;}else{	return orderDetailItemsId;}
	}

	public void setOrderDetailItemsId(String orderDetailItemsId) {
		this.orderDetailItemsId = orderDetailItemsId;
	}

	@Override
	public String toString()
	{
		return "NotPrintedOrderDetailItemsToPrinter [orderDetailItemsId=" + orderDetailItemsId + ", printersId=" + printersId + ", orderHeaderId=" + orderHeaderId + 
				 ", localTime=" + localTime + "]";
	}


	public String getPrintersId()
	{
		 if(printersId != null && (printersId.length()==0 || printersId.equals("0"))){return null;}else{	return printersId;}
	}


	public void setPrintersId(String printersId)
	{
		this.printersId = printersId;
	}


	public String getOrderHeaderId() {
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}

	public void setOrderHeaderId(String orderHeaderId) {
		this.orderHeaderId = orderHeaderId;
	}

	@Override
	public void setBaseRelation(int baseId)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setBaseToObjectRelation(int baseToObjectId)
	{
		// TODO Auto-generated method stub
		
	}

	

	

}
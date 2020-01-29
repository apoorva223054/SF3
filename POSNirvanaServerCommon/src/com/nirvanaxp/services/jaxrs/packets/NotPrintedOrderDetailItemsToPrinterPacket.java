/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.catalog.items.NotPrintedOrderDetailItemsToPrinter;
@XmlRootElement(name = "NotPrintedOrderDetailItemsToPrinterPacket")
public class NotPrintedOrderDetailItemsToPrinterPacket extends PostPacket
{

	private List<NotPrintedOrderDetailItemsToPrinter> notPrintedOrderDetailItemsToPrinter;
	
	@Override
	public String toString()
	{
		return "NotPrintedOrderDetailItemsToPrinterPacket [notPrintedOrderDetailItemsToPrinter=" + notPrintedOrderDetailItemsToPrinter + ", isBaseLocationUpdate=" + isBaseLocationUpdate + "]";
	}

	public List<NotPrintedOrderDetailItemsToPrinter> getNotPrintedOrderDetailItemsToPrinter()
	{
		return notPrintedOrderDetailItemsToPrinter;
	}

	public void setNotPrintedOrderDetailItemsToPrinter(List<NotPrintedOrderDetailItemsToPrinter> notPrintedOrderDetailItemsToPrinter)
	{
		this.notPrintedOrderDetailItemsToPrinter = notPrintedOrderDetailItemsToPrinter;
	}

	private int isBaseLocationUpdate;

	

	public int getIsBaseLocationUpdate() {
		return isBaseLocationUpdate;
	}

	public void setIsBaseLocationUpdate(int isBaseLocationUpdate) {
		this.isBaseLocationUpdate = isBaseLocationUpdate;
	}

	

	 
}

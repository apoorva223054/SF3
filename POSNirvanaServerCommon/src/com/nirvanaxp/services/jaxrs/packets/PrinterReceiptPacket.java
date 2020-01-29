/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.locations.receipt.PrinterReceipt;
@XmlRootElement(name = "PrinterReceiptPacket")
public class PrinterReceiptPacket extends PostPacket
{
	private PrinterReceipt printerReceipt;

	public PrinterReceipt getPrinterReceipt()
	{
		return printerReceipt;
	}

	public void setPrinterReceipt(PrinterReceipt printerReceipt)
	{
		this.printerReceipt = printerReceipt;
	}

	@Override
	public String toString()
	{
		return "PrinterReceiptPacket [printerReceipt=" + printerReceipt + "]";
	}

}

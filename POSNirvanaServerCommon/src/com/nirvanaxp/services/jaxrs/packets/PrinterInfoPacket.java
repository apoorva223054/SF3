/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.printers.Printer;

@XmlRootElement(name = "PrinterInfoPacket")
public class PrinterInfoPacket extends PostPacket
{

	public List<Printer> printer;

	public List<Printer> getPrinterInformation()
	{
		return printer;
	}

	public void setPrinterInformation(List<Printer> printerInformation)
	{
		this.printer = printerInformation;
	}

	@Override
	public String toString()
	{
		final int maxLen = 10;
		return "PrinterInfoPacket [printer=" + (printer != null ? printer.subList(0, Math.min(printer.size(), maxLen)) : null) + "]";
	}

}

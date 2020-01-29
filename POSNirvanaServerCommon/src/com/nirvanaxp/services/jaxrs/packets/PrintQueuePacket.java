package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.email.PrintQueue;

@XmlRootElement(name = "PrintQueuePacket")
public class PrintQueuePacket extends PostPacket{
	
	List<PrintQueue> printQueueList;

	public List<PrintQueue> getPrintQueueList() {
		return printQueueList;
	}

	public void setPrintQueueList(List<PrintQueue> printQueueList) {
		this.printQueueList = printQueueList;
	}

	@Override
	public String toString() {
		return "PrintQueuePacket [printQueueList=" + printQueueList + "]";
	}
	
	

	
}

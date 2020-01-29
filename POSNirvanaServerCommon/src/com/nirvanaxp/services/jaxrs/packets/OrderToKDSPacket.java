package com.nirvanaxp.services.jaxrs.packets;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "OrderToKDSPacket")
public class OrderToKDSPacket extends StoreForwardPacket{
	
	private String printerId;
	private String status;
	private String locationId;
	//TODO Ankur: why is this int and not boolean?
	private int isOrderAhead;
	
	
	public int getIsOrderAhead() {
		return isOrderAhead;
	}
	public void setIsOrderAhead(int isOrderAhead) {
		this.isOrderAhead = isOrderAhead;
	}
	public String getPrinterId() {
		 if(printerId != null && (printerId.length()==0 || printerId.equals("0"))){return null;}else{	return printerId;}
	}
	public void setPrinterId(String printerId) {
		this.printerId = printerId;
	}
	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "OrderToKDSPacket [printerId=" + printerId + ", status="
				+ status + ", locationId=" + locationId + ", isOrderAhead="
				+ isOrderAhead + "]";
	}
	
	
	
	

}

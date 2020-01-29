package com.nirvanaxp.services.jaxrs;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.services.jaxrs.packets.PostPacket;
@XmlRootElement(name = "RequestOrderGetPacket")
public class RequestOrderGetPacket extends PostPacket{
	

	private String supplierId;
	private String poNumber;
	private  String businessId;
	private int startIndex;
	private int endIndex;
	private String date;
	
	public String getSupplierId()
	{
		 if(supplierId != null && (supplierId.length()==0 || supplierId.equals("0"))){return null;}else{	return supplierId;}
	}
	public void setSupplierId(String supplierId)
	{
		this.supplierId = supplierId;
	}
	public String getPoNumber()
	{
		return poNumber;
	}
	public void setPoNumber(String poNumber)
	{
		this.poNumber = poNumber;
	}
	public String getBusinessId()
	{
		return businessId;
	}
	public void setBusinessId(String businessId)
	{
		this.businessId = businessId;
	}
	public int getStartIndex()
	{
		return startIndex;
	}
	public void setStartIndex(int startIndex)
	{
		this.startIndex = startIndex;
	}
	public int getEndIndex()
	{
		return endIndex;
	}
	public void setEndIndex(int endIndex)
	{
		this.endIndex = endIndex;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	 
}

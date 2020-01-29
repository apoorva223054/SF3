package com.nirvanaxp.services.jaxrs.packets;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GetOrderPaymentDetailForBatchSettlePacket")
public class GetOrderPaymentDetailForBatchSettlePacket extends PostPacket{
	private int[] paymentGatewayIds;
	private String usersId;
	private String locationsId;
	private String startDate;
	private String endDate;
	private int[] paymentTypeId;
	public int[] getPaymentGatewayIds() {
		return paymentGatewayIds;
	}
	public void setPaymentGatewayIds(int[] paymentGatewayIds) {
		this.paymentGatewayIds = paymentGatewayIds;
	}
	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}
	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}
	public String getLocationsId() {
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}
	public void setLocationsId(String locationsId) {
		this.locationsId = locationsId;
	}
	
	public int[] getPaymentTypeId()
	{
		return paymentTypeId;
	}
	public void setPaymentTypeId(int[] paymentTypeId)
	{
		this.paymentTypeId = paymentTypeId;
	}
	public String getStartDate()
	{
		return startDate;
	}
	public void setStartDate(String startDate)
	{
		this.startDate = startDate;
	}
	public String getEndDate()
	{
		return endDate;
	}
	public void setEndDate(String endDate)
	{
		this.endDate = endDate;
	}
	@Override
	public String toString()
	{
		return "GetOrderPaymentDetailForBatchSettlePacket [paymentGatewayIds=" + Arrays.toString(paymentGatewayIds) + ", usersId=" + usersId + ", locationsId=" + locationsId + ", startDate="
				+ startDate + ", endDate=" + endDate + ", paymentTypeId=" + Arrays.toString(paymentTypeId) + "]";
	}
	 
		
}

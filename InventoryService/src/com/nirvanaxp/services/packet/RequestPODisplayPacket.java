package com.nirvanaxp.services.packet;

public class RequestPODisplayPacket {

	String orderStatusId;
	String challanNo;
	String requestId;
	public String getOrderStatusId() {
		if(orderStatusId != null && (orderStatusId.length()==0 || orderStatusId.equals("0"))){return null;}else{	return orderStatusId;}
	}
	public void setOrderStatusId(String orderStatusId) {
		this.orderStatusId = orderStatusId;
	}
	public String getChallanNo() {
		return challanNo;
	}
	public void setChallanNo(String challanNo) {
		this.challanNo = challanNo;
	}
	public String getRequestId() {
		 if(requestId != null && (requestId.length()==0 || requestId.equals("0"))){return null;}else{	return requestId;}
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	@Override
	public String toString() {
		return "RequestPODisplayPacket [orderStatusId=" + orderStatusId + ", challanNo=" + challanNo + ", requestId="
				+ requestId + "]";
	}
	
}

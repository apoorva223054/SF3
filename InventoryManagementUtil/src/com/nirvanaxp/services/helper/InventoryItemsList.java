package com.nirvanaxp.services.helper;

public class InventoryItemsList
{

	String requestOrderDetailsId;
	String inventoryId;
	int inventoryHistoryId;
	public int getInventoryHistoryId() {
		return inventoryHistoryId;
	}
	public void setInventoryHistoryId(int inventoryHistoryId) {
		this.inventoryHistoryId = inventoryHistoryId;
	}
	public String getRequestOrderDetailsId() {
		return requestOrderDetailsId;
	}
	public void setRequestOrderDetailsId(String requestOrderDetailsId) {
		this.requestOrderDetailsId = requestOrderDetailsId;
	}
	public String getInventoryId() {
		 if(inventoryId != null && (inventoryId.length()==0 || inventoryId.equals("0"))){return null;}else{	return inventoryId;}
	}
	public void setInventoryId(String inventoryId) {
		this.inventoryId = inventoryId;
	}
	@Override
	public String toString() {
		return "InventoryItemsList [requestOrderDetailsId="
				+ requestOrderDetailsId + ", inventoryId=" + inventoryId
				+ ", inventoryHistoryId=" + inventoryHistoryId + "]";
	}
	
	
}

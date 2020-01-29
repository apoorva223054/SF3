package com.nirvanaxp.services.jaxrs.packets;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.KDSToOrderDetailItemStatus;

@XmlRootElement(name = "KDSToOrderDetailItemStatusPacket")
public class KDSToOrderDetailItemStatusPacket extends PostPacket{
	
	private List<KDSToOrderDetailItemStatus> kdsToOrderDetailItemStatusList;
	
	private String orderHeaderId;
	private int isOrderBumped;
	private int isAllowedToUpdateInventory;
	private  String orderStatusId;
	public List<KDSToOrderDetailItemStatus> getKdsToOrderDetailItemStatusList() {
		return kdsToOrderDetailItemStatusList;
	}

	public void setKdsToOrderDetailItemStatusList(
			List<KDSToOrderDetailItemStatus> kdsToOrderDetailItemStatusList) {
		this.kdsToOrderDetailItemStatusList = kdsToOrderDetailItemStatusList;
	}

	public String getOrderHeaderId() {
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}

	public void setOrderHeaderId(String orderHeaderId) {
		this.orderHeaderId = orderHeaderId;
	}

	public int getIsOrderBumped() {
		return isOrderBumped;
	}

	public void setIsOrderBumped(int isOrderBumped) {
		this.isOrderBumped = isOrderBumped;
	}

	public int getIsAllowedToUpdateInventory() {
		return isAllowedToUpdateInventory;
	}

	public void setIsAllowedToUpdateInventory(int isAllowedToUpdateInventory) {
		this.isAllowedToUpdateInventory = isAllowedToUpdateInventory;
	}

	public String getOrderStatusId() {
		if(orderStatusId != null && (orderStatusId.length()==0 || orderStatusId.equals("0"))){return null;}else{	return orderStatusId;}
	}

	public void setOrderStatusId(String orderStatusId) {
		this.orderStatusId = orderStatusId;
	}

	
	

	 
	

}

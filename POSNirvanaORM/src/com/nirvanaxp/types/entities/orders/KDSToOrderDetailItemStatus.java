package com.nirvanaxp.types.entities.orders;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

@Entity
@Table(name = "kds_to_order_detail_item_status")
@XmlRootElement(name = "kds_to_order_detail_item_status")
public class KDSToOrderDetailItemStatus  extends POSNirvanaBaseClass{

	

	private static final long serialVersionUID = -7873680261368058348L;

	@Column(name = "order_detail_item_id")
	private String orderDetailItemId;
	
	@Column(name = "printer_id")
	private String printerId;
	
	@Column(name = "status_id")
	private int statusId;

	private transient String orderDetailStatusName;
	private transient String orderHeaderId;
	private transient String orderStatusId;
	
	@Column(name = "local_time")
	private String localTime;
	private transient int isOrderBumped;
	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
	public String getOrderDetailStatusName() {
		return orderDetailStatusName;
	}

	public void setOrderDetailStatusName(String orderDetailStatusName) {
		this.orderDetailStatusName = orderDetailStatusName;
	}

	public String getOrderDetailItemId() {
		 if(orderDetailItemId != null && (orderDetailItemId.length()==0 || orderDetailItemId.equals("0"))){return null;}else{	return orderDetailItemId;}
	}

	public void setOrderDetailItemId(String orderDetailItemId) {
		this.orderDetailItemId = orderDetailItemId;
	}

	public String getPrinterId() {
		 if(printerId != null && (printerId.length()==0 || printerId.equals("0"))){return null;}else{	return printerId;}
	}

	public void setPrinterId(String printerId) {
		this.printerId = printerId;
	}

	public int getStatusId() {
			return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
	

	public int getIsOrderBumped() {
		return isOrderBumped;
	}

	public void setIsOrderBumped(int isOrderBumped) {
		this.isOrderBumped = isOrderBumped;
	}

	public String getOrderHeaderId() {
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}

	public void setOrderHeaderId(String orderHeaderId) {
		this.orderHeaderId = orderHeaderId;
	}

	public String getOrderStatusId() {
		if(orderStatusId != null && (orderStatusId.length()==0 || orderStatusId.equals("0"))){return null;}else{	return orderStatusId;}
	}

	public void setOrderStatusId(String orderStatusId) {
		this.orderStatusId = orderStatusId;
	}

	@Override
	public String toString() {
		return "KDSToOrderDetailItemStatus [orderDetailItemId="
				+ orderDetailItemId + ", printerId=" + printerId
				+ ", statusId=" + statusId + ", orderDetailStatusName="
				+ orderDetailStatusName + ", orderHeaderId=" + orderHeaderId
				+ ", orderStatusId=" + orderStatusId + ", localTime="
				+ localTime + ", isOrderBumped=" + isOrderBumped + ", id=" + id
				+ ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", created=" + created + ", createdBy=" + createdBy
				+ ", status=" + status + ", getLocalTime()=" + getLocalTime()
				+ ", getOrderDetailStatusName()=" + getOrderDetailStatusName()
				+ ", getOrderDetailItemId()=" + getOrderDetailItemId()
				+ ", getPrinterId()=" + getPrinterId() + ", getStatusId()="
				+ getStatusId() + ", getIsOrderBumped()=" + getIsOrderBumped()
				+ ", getOrderHeaderId()=" + getOrderHeaderId()
				+ ", getOrderStatusId()=" + getOrderStatusId() + ", getId()="
				+ getId() + ", getUpdatedBy()=" + getUpdatedBy()
				+ ", getCreated()=" + getCreated() + ", getUpdated()="
				+ getUpdated() + ", getCreatedBy()=" + getCreatedBy()
				+ ", getStatus()=" + getStatus() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + "]";
	}

	
}

	
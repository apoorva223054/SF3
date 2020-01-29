package com.nirvanaxp.types.entities.email;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithBigIntWithGeneratedId;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithBigIntWithGeneratedId;
@Entity
@Table(name = "print_queue")
@XmlRootElement(name = "print_queue")
public class PrintQueue extends
POSNirvanaBaseClassWithBigIntWithGeneratedId{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7873680261368058348L;

	@Column(name = "print_string", nullable = false)
	private String printString;
	
	@Column(name = "order_id")
	private String orderId;
	
	@Column(name = "account_id")
	private int accountId;
	
	@Column(name = "location_id")
	private String locationId;
	
	@Column(name = "order_detail_item_id")
	private String orderDetailItemId;
	
	@Column(name = "printer_id")
	private String printerId;
	
	@Column(name = "local_time")
	private String localTime;
	
	@Column(name = "is_order_ahead")
	private int isOrderAhead;
	
	@Column(name = "schedule_date_time")
	private String scheduleDateTime;
	private transient String orderStatusId;
	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
	public String getPrintString() {
		return printString;
	}

	public void setPrintString(String printString) {
		this.printString = printString;
	}

	public String getOrderId() {
		 if(orderId != null && (orderId.length()==0 || orderId.equals("0"))){return null;}else{	return orderId;}
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
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

	public int getIsOrderAhead() {
		return isOrderAhead;
	}

	public void setIsOrderAhead(int isOrderAhead) {
		this.isOrderAhead = isOrderAhead;
	}

	public String getScheduleDateTime() {
		return scheduleDateTime;
	}

	public void setScheduleDateTime(String scheduleDateTime) {
		this.scheduleDateTime = scheduleDateTime;
	}

	public String getOrderStatusId() {
		if(orderStatusId != null && (orderStatusId.length()==0 || orderStatusId.equals("0"))){return null;}else{	return orderStatusId;}
	}

	public void setOrderStatusId(String orderStatusId) {
		this.orderStatusId = orderStatusId;
	}

	@Override
	public String toString() {
		return "PrintQueue [printString=" + printString + ", orderId="
				+ orderId + ", accountId=" + accountId + ", locationId="
				+ locationId + ", orderDetailItemId=" + orderDetailItemId
				+ ", printerId=" + printerId + ", localTime=" + localTime
				+ ", isOrderAhead=" + isOrderAhead + ", scheduleDateTime="
				+ scheduleDateTime + "]";
	}

	


}

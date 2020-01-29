/*
 * 
 */
package com.nirvanaxp.services.jaxrs;

import java.util.ArrayList;
import java.util.List;

import com.nirvanaxp.types.entities.orders.OrderDetailItem;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderDetailItemWithPrinter.
 */
public class OrderDetailItemWithPrinter {
	
	/** The order detail item list. */
	List<ArrayList<OrderDetailItem>> orderDetailItemList;
	
	/** The printer id. */
	private String printerId;
	
	/**
	 * Gets the printer id.
	 *
	 * @return the printer id
	 */
	public String getPrinterId() {
		 if(printerId != null && (printerId.length()==0 || printerId.equals("0"))){return null;}else{	return printerId;}
	}
	
	/**
	 * Sets the printer id.
	 *
	 * @param printerId the new printer id
	 */
	public void setPrinterId(String printerId) {
		this.printerId = printerId;
	}
	
	/**
	 * Gets the order detail item list.
	 *
	 * @return the order detail item list
	 */
	public List<ArrayList<OrderDetailItem>> getOrderDetailItemList() {
		return orderDetailItemList;
	}
	
	/**
	 * Sets the order detail item list.
	 *
	 * @param orderDetailItemList the new order detail item list
	 */
	public void setOrderDetailItemList(List<ArrayList<OrderDetailItem>> orderDetailItemList) {
		this.orderDetailItemList = orderDetailItemList;
	}
	
}

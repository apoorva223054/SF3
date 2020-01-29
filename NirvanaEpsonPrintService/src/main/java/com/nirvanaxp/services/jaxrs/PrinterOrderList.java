/*
 * 
 */
package com.nirvanaxp.services.jaxrs;

import java.util.List;

import com.nirvanaxp.types.entities.orders.OrderDetailItem;

// TODO: Auto-generated Javadoc
/**
 * The Class PrinterOrderList.
 */
public class PrinterOrderList {
	
	/** The total print count. */
	public static int totalPrintCount=0;
	
	/** The current print count. */
	public static int currentPrintCount=0;
	
	/** The current order detail item list. */
	public static List<OrderDetailItem> currentOrderDetailItemList=null;
	
	/** The printer id. */
	private String printerId;

	/** The order detail item. */
	private OrderDetailItem orderDetailItem;

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
	 * Gets the order detail item.
	 *
	 * @return the order detail item
	 */
	public OrderDetailItem getOrderDetailItem() {
		return orderDetailItem;
	}

	/**
	 * Sets the order detail item.
	 *
	 * @param orderDetailItem the new order detail item
	 */
	public void setOrderDetailItem(OrderDetailItem orderDetailItem) {
		this.orderDetailItem = orderDetailItem;
	}
	
	
	
 	@Override
	    public boolean equals(Object o) {
	        try {
	            if (printerId == ((PrinterOrderList) o).getPrinterId()) {
	                return true;
	            }
	        } catch (Exception e) {
	        }
	        return false;
	    }
	
}
package com.nirvanaxp.server.util;

import java.util.List;

import com.nirvanaxp.types.entities.orders.OrderDetailItem;

public class PrinterOrderList {
	

	
	public static int totalPrintCount=0;
	
	public static int currentPrintCount=0;
	
	public static List<OrderDetailItem> currentOrderDetailItemList=null;
	
	private String printerId;

	private OrderDetailItem orderDetailItem;

	public String getPrinterId() {
		 if(printerId != null && (printerId.length()==0 || printerId.equals("0"))){return null;}else{	return printerId;}
	}

	public void setPrinterId(String printerId) {
		this.printerId = printerId;
	}

	public OrderDetailItem getOrderDetailItem() {
		return orderDetailItem;
	}

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

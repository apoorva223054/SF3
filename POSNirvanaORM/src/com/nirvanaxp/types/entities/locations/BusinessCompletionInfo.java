/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.locations;

public class BusinessCompletionInfo
{

	@Override
	public String toString() {
		return "BusinessCompletionInfo [businessHours=" + businessHours
				+ ", setupLocations=" + setupLocations
				+ ", reservationSchedule=" + reservationSchedule
				+ ", businessSetting=" + businessSetting + ", manageUsers="
				+ manageUsers + ", printers=" + printers + ", voidReason="
				+ voidReason + ", tax=" + tax + ", discount=" + discount
				+ ", orderStatus=" + orderStatus + ", addProducts="
				+ addProducts + ", inventory=" + inventory
				+ ", employeeOperation=" + employeeOperation + ", feedback="
				+ feedback + "]";
	}

	private int businessHours;
	private int setupLocations;
	private int reservationSchedule;
	private int businessSetting;
	private int manageUsers;
	private int printers;
	private int voidReason;
	private int tax;
	private int discount;
	private int orderStatus;
	private int addProducts;
	private int inventory;
	private int employeeOperation;
	private int feedback;

	public int getBusinessHours()
	{
		return businessHours;
	}

	public void setBusinessHours(int businessHours)
	{
		this.businessHours = businessHours;
	}

	public int getSetupLocations()
	{
		return setupLocations;
	}

	public void setSetupLocations(int setupLocations)
	{
		this.setupLocations = setupLocations;
	}

	public int getReservationSchedule()
	{
		return reservationSchedule;
	}

	public void setReservationSchedule(int reservationSchedule)
	{
		this.reservationSchedule = reservationSchedule;
	}

	public int getBusinessSetting()
	{
		return businessSetting;
	}

	public void setBusinessSetting(int businessSetting)
	{
		this.businessSetting = businessSetting;
	}

	public int getManageUsers()
	{
		return manageUsers;
	}

	public void setManageUsers(int manageUsers)
	{
		this.manageUsers = manageUsers;
	}

	public int getPrinters()
	{
		return printers;
	}

	public void setPrinters(int printers)
	{
		this.printers = printers;
	}

	public int getVoidReason()
	{
		return voidReason;
	}

	public void setVoidReason(int voidReason)
	{
		this.voidReason = voidReason;
	}

	public int getTax()
	{
		return tax;
	}

	public void setTax(int tax)
	{
		this.tax = tax;
	}

	public int getDiscount()
	{
		return discount;
	}

	public void setDiscount(int discount)
	{
		this.discount = discount;
	}

	public int getOrderStatus()
	{
		return orderStatus;
	}

	public void setOrderStatus(int orderStatus)
	{
		this.orderStatus = orderStatus;
	}

	public int getAddProducts()
	{
		return addProducts;
	}

	public void setAddProducts(int addProducts)
	{
		this.addProducts = addProducts;
	}

	public int getInventory()
	{
		return inventory;
	}

	public void setInventory(int inventory)
	{
		this.inventory = inventory;
	}

	public int getEmployeeOperation()
	{
		return employeeOperation;
	}

	public void setEmployeeOperation(int employeeOperation)
	{
		this.employeeOperation = employeeOperation;
	}

	public int getFeedback()
	{
		return feedback;
	}

	public void setFeedback(int feedback)
	{
		this.feedback = feedback;
	}

}

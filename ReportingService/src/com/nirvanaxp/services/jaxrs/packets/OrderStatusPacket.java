/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

public class OrderStatusPacket
{

	private String busReady;
	private String orderCreated;
	private String orderPlaced;
	private String drinkServed;
	private String appetizerServed;
	private String foodServed;
	private String dessertServed;
	private String checkPaid;

	public String getBusReady()
	{
		return busReady;
	}

	public void setBusReady(String busReady)
	{
		this.busReady = busReady;
	}

	public String getOrderCreated()
	{
		return orderCreated;
	}

	public void setOrderCreated(String orderCreated)
	{
		this.orderCreated = orderCreated;
	}

	public String getOrderPlaced()
	{
		return orderPlaced;
	}

	public void setOrderPlaced(String orderPlaced)
	{
		this.orderPlaced = orderPlaced;
	}

	public String getDrinkServed()
	{
		return drinkServed;
	}

	public void setDrinkServed(String drinkServed)
	{
		this.drinkServed = drinkServed;
	}

	public String getAppetizerServed()
	{
		return appetizerServed;
	}

	public void setAppetizerServed(String appetizerServed)
	{
		this.appetizerServed = appetizerServed;
	}

	public String getFoodServed()
	{
		return foodServed;
	}

	public void setFoodServed(String foodServed)
	{
		this.foodServed = foodServed;
	}

	public String getDessertServed()
	{
		return dessertServed;
	}

	public void setDessertServed(String dessertServed)
	{
		this.dessertServed = dessertServed;
	}

	public String getCheckPaid()
	{
		return checkPaid;
	}

	public void setCheckPaid(String checkPaid)
	{
		this.checkPaid = checkPaid;
	}

	@Override
	public String toString()
	{
		return "OrderStatusPacket [busReady=" + busReady + ", orderCreated=" + orderCreated + ", orderPlaced=" + orderPlaced + ", drinkServed=" + drinkServed + ", appetizerServed=" + appetizerServed
				+ ", foodServed=" + foodServed + ", dessertServed=" + dessertServed + ", checkPaid=" + checkPaid + "]";
	}

}

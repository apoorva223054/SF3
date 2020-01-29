/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigInteger;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.OrderHeader;
import com.nirvanaxp.types.entities.recurringPayment.Subscription;
import com.nirvanaxp.types.entities.user.User;

@XmlRootElement(name = "OrderPacket")
public class OrderPacket extends PostPacket
{

	private OrderHeader orderHeader;

	private String unmergedLocationsId;

	private User user;

	private String globalUserId;
	private int cashOnDelivery;

	private int packetVersion;

	private Subscription subscription;

	private int idOfOrderHoldingClientObj;
	
	private int isDigitalMenu;
	
	private int isCustomerApp;
	
	private int isDiscountRemove;
	private String[] orderHeaderIds;
	

	public int getIsDiscountRemove() {
		return isDiscountRemove;
	}

	public void setIsDiscountRemove(int isDiscountRemove) {
		this.isDiscountRemove = isDiscountRemove;
	}

	
	

	public int getIdOfOrderHoldingClientObj()
	{
		return idOfOrderHoldingClientObj;
	}

	public void setIdOfOrderHoldingClientObj(int idOfOrderHoldingClientObj)
	{
		this.idOfOrderHoldingClientObj = idOfOrderHoldingClientObj;
	}

	public OrderHeader getOrderHeader()
	{
		return orderHeader;
	}

	public void setOrderHeader(OrderHeader orderHeader)
	{
		this.orderHeader = orderHeader;
	}

	public String getGlobalUserId()
	{
		return globalUserId;
	}

	public void setGlobalUserId(String globalUserId)
	{
		this.globalUserId = globalUserId;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public String getUnmergedLocationsId()
	{
		return unmergedLocationsId;
	}

	public void setUnmergedLocationsId(String unmergedLocationsId)
	{
		this.unmergedLocationsId = unmergedLocationsId;
	}

	public int getPacketVersion()
	{
		return packetVersion;
	}

	public void setPacketVersion(int packetVersion)
	{
		this.packetVersion = packetVersion;
	}

	/**
	 * @return the subscription
	 */
	public Subscription getSubscription()
	{
		return subscription;
	}

	/**
	 * @param subscription
	 *            the subscription to set
	 */
	public void setSubscription(Subscription subscription)
	{
		this.subscription = subscription;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	
	
	
	 

	public int getIsDigitalMenu()
	{
		return isDigitalMenu;
	}

	public void setIsDigitalMenu(int isDigitalMenu)
	{
		this.isDigitalMenu = isDigitalMenu;
	}

	public String[] getOrderHeaderIds() {
		return orderHeaderIds;
	}

	public void setOrderHeaderIds(String[] orderHeaderIds) {
		this.orderHeaderIds = orderHeaderIds;
	}

	public int getCashOnDelivery() {
		return cashOnDelivery;
	}

	public void setCashOnDelivery(int cashOnDelivery) {
		this.cashOnDelivery = cashOnDelivery;
	}


	

	@Override
	public String toString() {
		return "OrderPacket [orderHeader=" + orderHeader
				+ ", unmergedLocationsId=" + unmergedLocationsId + ", user="
				+ user + ", globalUserId=" + globalUserId + ", cashOnDelivery="
				+ cashOnDelivery + ", packetVersion=" + packetVersion
				+ ", subscription=" + subscription
				+ ", idOfOrderHoldingClientObj=" + idOfOrderHoldingClientObj
				+ ", isDigitalMenu=" + isDigitalMenu + ", isCustomerApp="
				+ isCustomerApp + ", isDiscountRemove=" + isDiscountRemove
				+ ", orderHeaderIds=" + Arrays.toString(orderHeaderIds) + "]";
	}

	public int getIsCustomerApp() {
		return isCustomerApp;
	}

	public void setIsCustomerApp(int isCustomerApp) {
		this.isCustomerApp = isCustomerApp;
	}
	

}

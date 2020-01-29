package com.nirvanaxp.services.jaxrs.packets;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DriverOrderSummaryPacket")
public class DriverOrderSummaryPacket {
	
	private int totalDriverOrders;
	private int totalDeliveredOrders;
	private int totalPendingOrders;
	private BigDecimal balanceDue;
	
	
	public BigDecimal getBalanceDue()
	{
		return balanceDue;
	}

	public void setBalanceDue(BigDecimal balanceDue)
	{
		this.balanceDue = balanceDue;
	}

	public int getTotalPendingOrders()
	{
		return totalPendingOrders;
	}

	public void setTotalPendingOrders(int totalPendingOrders)
	{
		this.totalPendingOrders = totalPendingOrders;
	}

	public int getTotalDeliveredOrders()
	{
		return totalDeliveredOrders;
	}

	public void setTotalDeliveredOrders(int totalDeliveredOrders)
	{
		this.totalDeliveredOrders = totalDeliveredOrders;
	}

	public int getTotalDriverOrders()
	{
		return totalDriverOrders;
	}

	public void setTotalDriverOrders(int totalDriverOrders)
	{
		this.totalDriverOrders = totalDriverOrders;
	}
	

}

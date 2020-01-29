/*
 * 
 */
package com.nirvanaxp.services.data;

import com.nirvanaxp.services.jaxrs.packets.InventoryPostPacket;
import com.nirvanaxp.types.entities.orders.OrderHeader;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderHeaderWithInventoryPostPacket.
 */
public class OrderHeaderWithInventoryPostPacket {
	
	/** The order header. */
	private OrderHeader orderHeader;
	
	/** The inventory post packet. */
	private InventoryPostPacket inventoryPostPacket;
	
	/**
	 * Instantiates a new order header with inventory post packet.
	 *
	 * @param oh the oh
	 * @param ipp the ipp
	 */
	public OrderHeaderWithInventoryPostPacket(OrderHeader oh, InventoryPostPacket ipp)
	{
		this.orderHeader = oh;
		this.inventoryPostPacket = ipp;
	}
	
	/**
	 * Gets the order header.
	 *
	 * @return the order header
	 */
	public OrderHeader getOrderHeader()
	{
		return orderHeader;
	}
	
	/**
	 * Gets the inventory post packet.
	 *
	 * @return the inventory post packet
	 */
	public InventoryPostPacket getInventoryPostPacket()
	{
		return inventoryPostPacket;
	}

}

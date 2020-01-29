package com.nirvanaxp.services.jaxrs;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.services.jaxrs.packets.PostPacket;
import com.nirvanaxp.types.entities.orders.ShiftSlots;

// TODO: Auto-generated Javadoc
/**
 * The Class HoldShiftSlotPacket.
 */
@XmlRootElement(name = "HoldShiftSlotPacket")
public class HoldShiftSlotPacket extends PostPacket
{

	/** The shift slots. */
	private ShiftSlots shiftSlots;

	/**
	 * Gets the shift slots.
	 *
	 * @return the shift slots
	 */
	public ShiftSlots getShiftSlots()
	{
		return shiftSlots;
	}

	/**
	 * Sets the shift slots.
	 *
	 * @param shiftSlots
	 *            the new shift slots
	 */
	public void setShiftSlots(ShiftSlots shiftSlots)
	{
		this.shiftSlots = shiftSlots;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nirvanaxp.services.jaxrs.packets.PostPacket#toString()
	 */
	@Override
	public String toString()
	{
		return "HoldShiftSlotPacket [shiftSlots=" + shiftSlots + "]";
	}

}

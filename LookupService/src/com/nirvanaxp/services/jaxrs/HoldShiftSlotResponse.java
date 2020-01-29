package com.nirvanaxp.services.jaxrs;

import com.nirvanaxp.types.entities.orders.ShiftSlots;

// TODO: Auto-generated Javadoc
/**
 * The Class HoldShiftSlotResponse.
 */
public class HoldShiftSlotResponse
{

	/** The shift slots. */
	private ShiftSlots shiftSlots;

	/** The shift holding client id. */
	private int shiftHoldingClientId;

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

	/**
	 * Gets the shift holding client id.
	 *
	 * @return the shift holding client id
	 */
	public int getShiftHoldingClientId()
	{
		return shiftHoldingClientId;
	}

	/**
	 * Sets the shift holding client id.
	 *
	 * @param shiftHoldingClientId
	 *            the new shift holding client id
	 */
	public void setShiftHoldingClientId(int shiftHoldingClientId)
	{
		this.shiftHoldingClientId = shiftHoldingClientId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "HoldShiftSlotPacket [shiftSlots=" + shiftSlots + ", shiftHoldingClientId=" + shiftHoldingClientId + "]";
	}
}

package com.nirvanaxp.services.jaxrs;

import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.services.jaxrs.packets.PostPacket;

// TODO: Auto-generated Javadoc
/**
 * The Class PrinterDetailPacket.
 */
@XmlRootElement(name = "PrinterDetailPacket")
public class PrinterDetailPacket extends PostPacket
{

	/** The ip address. */
	private String ipAddress;

	/** The locations id. */
	private String locationsId;

	/**
	 * Gets the ip address.
	 *
	 * @return the ip address
	 */
	public String getIpAddress()
	{
		return ipAddress;
	}

	/**
	 * Sets the ip address.
	 *
	 * @param ipAddress
	 *            the new ip address
	 */
	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	/**
	 * Gets the locations id.
	 *
	 * @return the locations id
	 */
	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	/**
	 * Sets the locations id.
	 *
	 * @param locationsId
	 *            the new locations id
	 */
	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nirvanaxp.services.jaxrs.packets.PostPacket#toString()
	 */
	@Override
	public String toString()
	{
		return "PrinterDetailPacket [ipAddress=" + ipAddress + ", locationsId=" + locationsId + "]";
	}

}

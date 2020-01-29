package com.nirvanaxp.services.jaxrs.packets;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.ws.rs.PathParam;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.orders.BatchDetail;

@XmlRootElement(name = "BatchDetailUpdatePacket")
public class BatchDetailUpdatePacket extends StoreForwardPacket{

	private int id;
	
	private String startDateTime;

	private String endDateTime;
	
	private String tipCalculatedStatus;
	
	private String locationId;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getStartDateTime()
	{
		return startDateTime;
	}

	public void setStartDateTime(String startDateTime)
	{
		this.startDateTime = startDateTime;
	}

	public String getEndDateTime()
	{
		return endDateTime;
	}

	public void setEndDateTime(String endDateTime)
	{
		this.endDateTime = endDateTime;
	}

	public String getTipCalculatedStatus()
	{
		return tipCalculatedStatus;
	}

	public void setTipCalculatedStatus(String tipCalculatedStatus)
	{
		this.tipCalculatedStatus = tipCalculatedStatus;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}
	

	

}

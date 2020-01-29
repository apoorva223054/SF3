package com.nirvanaxp.types.entities.locations;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;


/**
 * The persistent class for the location_setting database table.
 * 
 */

@Entity
@Table(name="location_batch_timing")
@XmlRootElement(name = "location_batch_timing")
public class LocationBatchTiming extends POSNirvanaBaseClassWithoutGeneratedIds {
	private static final long serialVersionUID = 1L;

	@Column(name="start_time")
	private String startTime;

	@Column(name="end_time")
	private String endtime;
	
	@Column(name="location_setting_id")
	private String locationSettingId;

	public String getStartTime()
	{
		return startTime;
	}

	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}

	public String getEndtime()
	{
		return endtime;
	}

	public void setEndtime(String endtime)
	{
		this.endtime = endtime;
	}

	public String getLocationSettingId()
	{
		 if(locationSettingId != null && (locationSettingId.length()==0 || locationSettingId.equals("0"))){return null;}else{	return locationSettingId;}
	}

	public void setLocationSettingId(String locationSettingId)
	{
		this.locationSettingId = locationSettingId;
	}

	 
	
 
 }
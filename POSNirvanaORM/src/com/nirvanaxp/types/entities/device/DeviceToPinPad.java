package com.nirvanaxp.types.entities.device;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClassWithoutGeneratedIds;


/**
 * The persistent class for the device_to_register database table.
 * 
 */
@Entity
@Table(name="device_to_pin_pad")
@NamedQuery(name="DeviceToPinPad.findAll", query="SELECT d FROM DeviceToPinPad d")
public class DeviceToPinPad extends POSNirvanaBaseClassWithoutGeneratedIds implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="device_id")
	private String deviceId;

	@Column(name="pin_pad")
	private String pinPad;
	
	@Column(name="locations_id")
	private String locationsId;

	
	public DeviceToPinPad() {
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

 

	public String getLocationsId() {
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	public void setLocationsId(String locationsId) {
		this.locationsId = locationsId;
	}

 

	public String getPinPad() {
		 if(pinPad != null && (pinPad.length()==0 || pinPad.equals("0"))){return null;}else{	return pinPad;}
	}

	public void setPinPad(String pinPad) {
		this.pinPad = pinPad;
	}

	@Override
	public String toString()
	{
		return "DeviceToPinPad [deviceId=" + deviceId + ", pinPad=" + pinPad + ", locationsId=" + locationsId + "]";
	}

	 
	
}
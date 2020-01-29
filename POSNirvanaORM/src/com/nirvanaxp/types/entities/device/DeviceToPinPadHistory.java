package com.nirvanaxp.types.entities.device;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;


/**
 * The persistent class for the device_to_register_history database table.
 * 
 */
@Entity
@Table(name="device_to_pin_pad_history")
@NamedQuery(name="DeviceToPinPadHistory.findAll", query="SELECT d FROM DeviceToPinPadHistory d")
public class DeviceToPinPadHistory extends POSNirvanaBaseClass implements Serializable {
	private static final long serialVersionUID = 1L;

	

	@Column(name="device_id")
	private String deviceId;

	@Column(name="device_to_register_id")
	private int deviceToRegisterId;

	@Column(name="register_id")
	private String registerId;

	@Column(name="locations_id")
	private String locationsId;

	public DeviceToPinPadHistory() {
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getDeviceToRegisterId() {
		return this.deviceToRegisterId;
	}

	public void setDeviceToRegisterId(int deviceToRegisterId) {
		this.deviceToRegisterId = deviceToRegisterId;
	}

	public String getRegisterId() {
		 if(registerId != null && (registerId.length()==0 || registerId.equals("0"))){return null;}else{	return registerId;}
	}

	public void setRegisterId(String registerId) {
		this.registerId = registerId;
	}

	public String getLocationsId() {
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	public void setLocationsId(String locationsId) {
		this.locationsId = locationsId;
	}

	@Override
	public String toString() {
		return "DeviceToRegisterHistory [deviceId=" + deviceId
				+ ", deviceToRegisterId=" + deviceToRegisterId
				+ ", registerId=" + registerId + ", locationsId=" + locationsId
				+ "]";
	}
	
	

}
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
@Table(name="device_to_register_history")
@NamedQuery(name="DeviceToRegisterHistory.findAll", query="SELECT d FROM DeviceToRegisterHistory d")
public class DeviceToRegisterHistory extends POSNirvanaBaseClass implements Serializable {
	private static final long serialVersionUID = 1L;

	

	@Column(name="device_id")
	private String deviceId;

	@Column(name="device_to_register_id")
	private String deviceToRegisterId;

	@Column(name="register_id")
	private String registerId;

	@Column(name="locations_id")
	private String locationsId;

	public DeviceToRegisterHistory() {
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceToRegisterId() {
		if(deviceToRegisterId != null && (deviceToRegisterId.length()==0 || deviceToRegisterId.equals("0"))){return null;}else{	return deviceToRegisterId;}
	}

	public void setDeviceToRegisterId(String deviceToRegisterId) {
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
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
@Table(name="device_to_register")
@NamedQuery(name="DeviceToRegister.findAll", query="SELECT d FROM DeviceToRegister d")
public class DeviceToRegister extends POSNirvanaBaseClassWithoutGeneratedIds implements Serializable {
	private static final long serialVersionUID = 1L;


	
	@Column(name="device_id")
	private String deviceId;

	@Column(name="register_id")
	private String registerId;
	
	@Column(name="locations_id")
	private String locationsId;

	
	public DeviceToRegister() {
	}

	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
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
		return "DeviceToRegister [deviceId=" + deviceId + ", registerId="
				+ registerId + ", locationsId=" + locationsId + "]";
	}
	

	
}
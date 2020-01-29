package com.nirvanaxp.services.authentication;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LoginPacket")
public class LoginPacket {
	String username; 
	String password;
	String appType; 
	String deviceId;
	int deviceTypeId;
	String deviceName; 
	String versionInfo; 
	int businessId; 
	String scope;
	String ipAddress;
	String rolesName;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAppType() {
		return appType;
	}
	public void setAppType(String appType) {
		this.appType = appType;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public int getDeviceTypeId() {
		return deviceTypeId;
	}
	public void setDeviceTypeId(int deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getVersionInfo() {
		return versionInfo;
	}
	public void setVersionInfo(String versionInfo) {
		this.versionInfo = versionInfo;
	}
	public int getBusinessId() {
		return businessId;
	}
	public void setBusinessId(int businessId) {
		this.businessId = businessId;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getRolesName() {
		return rolesName;
	}
	public void setRolesName(String rolesName) {
		this.rolesName = rolesName;
	}
	@Override
	public String toString() {
		return "LoginPacket [username=" + username + ", password=" + password + ", appType=" + appType + ", deviceId="
				+ deviceId + ", deviceTypeId=" + deviceTypeId + ", deviceName=" + deviceName + ", versionInfo="
				+ versionInfo + ", businessId=" + businessId + ", scope=" + scope + ", ipAddress=" + ipAddress
				+ ", rolesName=" + rolesName + "]";
	}
	
	

}

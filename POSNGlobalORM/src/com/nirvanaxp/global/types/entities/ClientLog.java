package com.nirvanaxp.global.types.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.nirvanaxp.global.types.entities.partners.TimezoneTime;

/**
 * Entity implementation class for Entity: ClientLog
 *
 */
@Entity
@Table(name="client_log")
@XmlRootElement(name = "ClientLog")
public class ClientLog implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "log_level")
	private String logLevel;
	
	@Column(name = "account_id")
	private int accountId;
	
	@Column(name = "business_id")
	private String businessId;
	
	@Column(name = "logged_in_user_id")
	private String loggedInUserId;
	
	@Column(name = "pin_in_user_id")
	private String pinInUserId;
	
	@Column(name = "session_id")
	private String sessionId;
	
	@Column(name = "device_id")
	private String deviceId;
	
	@Column(name = "device_type")
	private String deviceType;
	
	@Column(name = "remote_ip_address")
	private String remoteIPAddress;
	
	@Column(name = "function_name")
	private String functionName;
	
	@Column(name = "class_name")
	private String className;
	
	@Transient	
	private long clientDateTime;
	
	@Column(name = "client_date_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date clientLogTime;
	
	@Column(name = "server_date_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date serverDateTime;
	
	@Column(name = "log_string")
	private String logString;
	
	@Column(name = "ip_address")
	private String ipAddress;
	
	private static final long serialVersionUID = 1L;

	public ClientLog() {
		super();
	}   
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}   
	public String getLogLevel() {
		return this.logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}   
	public int getAccountId() {
		return this.accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}   
	public String getBusinessId() {
		return this.businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}   
	public String getLoggedInUserId() {
		return this.loggedInUserId;
	}

	public void setLoggedInUserId(String loggedInUserId) {
		this.loggedInUserId = loggedInUserId;
	}   
  
	public String getPinInUserId()
	{
		return pinInUserId;
	}
	public void setPinInUserId(String pinInUserId)
	{
		this.pinInUserId = pinInUserId;
	}
	public String getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}   
	public String getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}   
	public String getDeviceType() {
		return this.deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}  
	
	public String getRemoteIPAddress() {
		return this.remoteIPAddress;
	}
	@JsonIgnore
	public void setRemoteIPAddress(String remoteIpAddress) {
		this.remoteIPAddress = remoteIpAddress;
	}  
	
	public String getFunctionName() {
		return this.functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}   
	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}  
	
	public long getClientDateTime() {
		return this.clientDateTime;
	}

	public void setClientDateTime(long clientDateTime) {
		this.clientDateTime = clientDateTime;
	}  
	
	public Date getClientLogTime()
	{
		return this.clientLogTime;
	}
	
	@JsonIgnore
	public void setClientLogTime()
	{
		this.clientLogTime = new Date(getClientDateTime());
	}
	
	public Date getServerDateTime() {
		return serverDateTime;
	}
	
	@JsonIgnore
	public void setServerDateTime(Date serverDateTime) {
		this.serverDateTime = serverDateTime;
	}
	
	public String getLogString() {
		return this.logString;
	}

	public void setLogString(String logString) {
		this.logString = logString;
	}
	
	public String getIpAddress()
	{
		return ipAddress;
	}
	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}
	
	@Override
	public String toString()
	{
		return String.join(" ", "\nRemote IP Address:", remoteIPAddress, "\n\nClientLog \n[loggedInUserId=" + loggedInUserId, "pinInUserId=" + pinInUserId, "\naccountId=" + accountId,
				"\nlocationId=" + businessId, "\nIP Address=", ipAddress, "\ndeviceId=", deviceId, "\ndeviceType=", deviceType, "\nsessionId=", sessionId, "\nclientDateTime=", getClientLogTime()==null?"no date":getClientLogTime().toString(),
				"\nfunctionName=", functionName, "\nclassName=", className, "\nlogString=", logString, "]\n");
	}

	public void update(HttpServletRequest httpRequest)
	{

		String remoteIP = ((httpRequest != null) ? httpRequest.getRemoteAddr() : "HTTP Request is Null");
		setRemoteIPAddress(remoteIP);
		setClientLogTime();
		setServerDateTime(new Date(new TimezoneTime().getGMTTimeInMilis()));
	}
   
}

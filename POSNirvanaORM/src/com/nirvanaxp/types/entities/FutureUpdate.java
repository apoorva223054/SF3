package com.nirvanaxp.types.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "future_update")
@XmlRootElement(name = "future_update")
public class FutureUpdate extends POSNirvanaBaseClass implements Serializable
{
	private static final long serialVersionUID = 1L;


	@Column(name = "service_name")
	private String serviceName;

	@Column(name = "operation_name")
	private String operationName;

	@Column(name = "packet_string")
	private String packetString;

	@Column(name = "schema_name")
	private String schemaName;
	
	@Column(name = "session_id")
	private String sessionId;
	
	@Column(name = "date")
	private String date;
	
	@Column(name = "location_id")
	private String locationId;
	
	
	@Column(name = "global_item_id")
	private String globalItemId;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getPacketString() {
		return packetString;
	}

	public void setPacketString(String packetString) {
		this.packetString = packetString;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	

	 

	public String getGlobalItemId()
	{
		 if(globalItemId != null && (globalItemId.length()==0 || globalItemId.equals("0"))){return null;}else{	return globalItemId;}
	}

	public void setGlobalItemId(String globalItemId)
	{
		this.globalItemId = globalItemId;
	}

	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "FutureUpdate [serviceName=" + serviceName + ", operationName=" + operationName + ", packetString="
				+ packetString + ", schemaName=" + schemaName + ", sessionId=" + sessionId + ", date=" + date
				+ ", locationId=" + locationId + ", globalItemId=" + globalItemId + "]";
	}

	

	 
}

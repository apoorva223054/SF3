/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.orders;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.POSNirvanaBaseClass;
import com.nirvanaxp.types.entities.protocol.RelationalEntities;

/**
 * The persistent class for the reservation_schedule database table.
 * 
 */
@Entity
@Table(name = "publisher")
@XmlRootElement(name = "publisher")
public class Publisher extends POSNirvanaBaseClass {
	private static final long serialVersionUID = 1L;
	 
	public Publisher() {
		// TODO Auto-generated constructor stub
	}

	@Column(name = "service_name", nullable = false)
	private String serviceName;
	
	@Column(name = "service_url", nullable = false)
	private String serviceURL;

	@Column(name = "packet" )
	private String packet;
	
	@Column(name = "location_id", nullable = false)
	private String locationId;
	
	@Column(name = "account_id", nullable = false)
	private int accountId;
	
	@Column(name = "method_type", nullable = false)
	private String methodType;
	
	@Column(name = "response")
	private String response;
	
	@Column(name = "retry_count")
	private int retryCount;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	 

	public String getResponse()
	{
		return response;
	}

	public void setResponse(String response)
	{
		this.response = response;
	}


	public String getLocationId() {
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getServiceURL()
	{
		return serviceURL;
	}

	public void setServiceURL(String serviceURL)
	{
		this.serviceURL = serviceURL;
	}

	public String getPacket()
	{
		return packet;
	}

	public void setPacket(String packet)
	{
		this.packet = packet;
	}

	
	public int getAccountId()
	{
		return accountId;
	}

	public void setAccountId(int accountId)
	{
		this.accountId = accountId;
	}

	public String getMethodType()
	{
		return methodType;
	}

	public void setMethodType(String methodType)
	{
		this.methodType = methodType;
	}
	

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	@Override
	public String toString() {
		return "Publisher [serviceName=" + serviceName + ", serviceURL=" + serviceURL + ", packet=" + packet
				+ ", locationId=" + locationId + ", accountId=" + accountId + ", methodType=" + methodType
				+ ", response=" + response + ", retryCount=" + retryCount + "]";
	}

	


}
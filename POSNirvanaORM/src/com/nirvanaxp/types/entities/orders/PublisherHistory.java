/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.orders;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.global.types.entities.partners.TimezoneTime;
import com.nirvanaxp.types.entities.POSNirvanaBaseClass;

/**
 * The persistent class for the reservation_schedule database table.
 * 
 */
@Entity
@Table(name = "publisher_history")
@XmlRootElement(name = "publisher_history")
public class PublisherHistory extends POSNirvanaBaseClass  {
	private static final long serialVersionUID = 1L;
	 
	public PublisherHistory() {
		// TODO Auto-generated constructor stub
	}
	@Column(name = "publisher_id", nullable = false)
	private int publisherId;
	
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

	
	public int getPublisherId()
	{
		return publisherId;
	}

	public void setPublisherId(int publisherId)
	{
		this.publisherId = publisherId;
	}

	@Override
	public String toString()
	{
		return "Publisher [serviceName=" + serviceName + ", serviceURL=" + serviceURL + ", packet=" + packet + ", locationId=" + locationId + ", accountId=" + accountId + ", methodType=" + methodType
				+ ", response=" + response + "]";
	}

	public PublisherHistory insertPublisherHistory(EntityManager em, Publisher p){
		
		PublisherHistory history = new PublisherHistory();
		history.setAccountId(p.getAccountId());
		history.setCreated(p.getCreated());
		history.setCreatedBy(p.getCreatedBy());
		history.setLocationId(p.getLocationId());
		history.setMethodType(p.getMethodType());
		history.setPacket(p.getPacket());
		history.setResponse(p.getPacket());
		history.setServiceName(p.getServiceName());
		history.setServiceURL(p.getServiceURL());
		history.setStatus(p.getStatus());
		history.setPublisherId(p.getId());
		history.setUpdated(new Date(new TimezoneTime().getGMTTimeInMilis()));
		history.setUpdatedBy(p.getUpdatedBy());
		em.persist(history);
		return history;
		
	}
	


}
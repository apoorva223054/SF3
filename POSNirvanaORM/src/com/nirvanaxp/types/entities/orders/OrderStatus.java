/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.orders;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The persistent class for the order_status database table.
 * 
 */
@Entity
@Table(name = "order_status")
@XmlRootElement(name = "order_status")
public class OrderStatus implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(nullable = false, length = 256)
	private String description;

	@Column(name = "display_name", nullable = false, length = 64)
	private String displayName;

	@Column(name = "display_sequence")
	private int displaySequence;

	@Column(name = "is_server_driven", nullable = false)
	private int isServerDriven;
	
	@Column(name = "is_order_tracking", nullable = false)
	private int isOrderTracking;

	

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(nullable = false, length = 32)
	private String name;

	@Column(name = "order_source_group_id", nullable = false)
	private String orderSourceGroupId;

	@Column(name = "status_colour", nullable = false, length = 8)
	private String statusColour;

	/*
	 * //@Column(name = "guest_message") private String guestMessage;
	 * 
	 * //@Column(name = "vendor_message") private String vendorMessage;
	 * 
	 * //@Column(name = "vendor_number") private String vendorNumber;
	 * 
	 * //@Column(name = "is_text") private int isText;
	 * 
	 * //@Column(name = "is_email") private int isEmail;
	 */

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(nullable = false, length = 1)
	private String status;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "is_send_sms")
	private int isSendSms;
	
	@Column(name = "template_id")
	private int templateId;
 

	

	public int getIsSendSms() {
		return isSendSms;
	}

	public void setIsSendSms(int isSendSms) {
		this.isSendSms = isSendSms;
	}

	public int getTemplateId() {
		return templateId;
	}

	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}
	
	public OrderStatus()
	{
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	 
	public String getId() {
		if (id != null && (id.length() == 0 || id.equals("0"))) {
			return null;
		} else {
			return id;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getDisplayName()
	{
		return this.displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public int getDisplaySequence()
	{
		return this.displaySequence;
	}

	public void setDisplaySequence(int displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public int getIsServerDriven()
	{
		return this.isServerDriven;
	}

	public void setIsServerDriven(int isServerDriven)
	{
		this.isServerDriven = isServerDriven;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getOrderSourceGroupId()
	{
		return this.orderSourceGroupId;
	}

	public void setOrderSourceGroupId(String orderSourceId)
	{
		this.orderSourceGroupId = orderSourceId;
	}

	public String getStatusColour()
	{
		return this.statusColour;
	}

	public void setStatusColour(String statusColour)
	{
		this.statusColour = statusColour;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getImageUrl()
	{
		return imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}

	public Date getCreated()
	{
		return created;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

	

	/*
	 * public String getGuestMessage() { return guestMessage; }
	 * 
	 * public void setGuestMessage(String guestMessage) { this.guestMessage =
	 * guestMessage; }
	 * 
	 * public String getVendorMessage() { return vendorMessage; }
	 * 
	 * public void setVendorMessage(String vendorMessage) { this.vendorMessage =
	 * vendorMessage; }
	 * 
	 * public String getVendorNumber() { return vendorNumber; }
	 */

	/*
	 * public void setVendorNumber(String vendorNumber) { this.vendorNumber =
	 * vendorNumber; }
	 * 
	 * public int getIsText() { return isText; }
	 * 
	 * public void setIsText(int isText) { this.isText = isText; }
	 * 
	 * public int getIsEmail() { return isEmail; }
	 * 
	 * public void setIsEmail(int isEmail) { this.isEmail = isEmail; }
	 */
	
	@Override
	public String toString()
	{
		return "OrderStatus [id=" + id + ", created=" + created + ", createdBy=" + createdBy + ", description=" + description + ", displayName=" + displayName + ", displaySequence=" + displaySequence
				+ ", isServerDriven=" + isServerDriven + ", isOrderTracking=" + isOrderTracking + ", locationsId=" + locationsId + ", name=" + name + ", orderSourceGroupId=" + orderSourceGroupId
				+ ", statusColour=" + statusColour + ", updated=" + updated + ", updatedBy=" + updatedBy + ", status=" + status + ", imageUrl=" + imageUrl + "]";
	}

	public int getIsOrderTracking()
	{
		return isOrderTracking;
	}

	public void setIsOrderTracking(int isOrderTracking)
	{
		this.isOrderTracking = isOrderTracking;
	}

}
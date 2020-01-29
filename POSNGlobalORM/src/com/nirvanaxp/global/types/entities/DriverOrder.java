/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities;

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
 * The persistent class for the business database table.
 * 
 */
@Entity
@Table(name = "driver_order")
@XmlRootElement(name = "driver_order")
public class DriverOrder implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "order_number")
	private String orderNumber;
	
	@Column(name = "order_id")
	private String orderId;

	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "status_id")
	private String statusId;
	
	@Column(name = "status_name")
	private String statusName;

	@Column(name = "business_name")
	private String businessName;
	
	@Column(name = "business_address")
	private String businessAddress;
	
	@Column(name = "customer_address")
	private String customerAddress;
	
	@Column(name = "time")
	private Date time;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;
	
	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "nxp_access_token")
	private String nxpAccessToken;
	
	@Column(name = "business_id")
	private int businessId;
	

	@Column(name = "locations_id")
	private String locationsId;
	
	@Column(name = "account_id")
	private int accountId;
	
	@Column(name = "driver_id")
	private String driverId;
	
	@Column(name = "schedule_date_time")
	private  String scheduleDateTime;
	
	


	 


	public String getDriverId()
	{
		 if(driverId != null && (driverId.length()==0 || driverId.equals("0"))){return null;}else{	return driverId;}
	}


	public void setDriverId(String driverId)
	{
		this.driverId = driverId;
	}


	public int getAccountId()
	{
		return accountId;
	}
	
	
	public void setAccountId(int accountId)
	{
		this.accountId = accountId;
	}
	
	public int getBusinessId()
	{
		return businessId;
	}

	public void setBusinessId(int businessId)
	{
		this.businessId = businessId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrderNumber() {
		 if(orderNumber != null && (orderNumber.length()==0 || orderNumber.equals("0"))){return null;}else{	return orderNumber;}
	}


	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}


	public String getOrderId() {
		 if(orderId != null && (orderId.length()==0 || orderId.equals("0"))){return null;}else{	return orderId;}
	}


	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getBusinessAddress() {
		return businessAddress;
	}

	public void setBusinessAddress(String businessAddress) {
		this.businessAddress = businessAddress;
	}

	public String getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
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


	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getNxpAccessToken() {
		return nxpAccessToken;
	}

	public void setNxpAccessToken(String nxpAccessToken) {
		this.nxpAccessToken = nxpAccessToken;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getStatusId() {
		 if(statusId != null && (statusId.length()==0 || statusId.equals("0"))){return null;}else{	return statusId;}
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	
	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}
	
 

	public String getScheduleDateTime() {
		return scheduleDateTime;
	}


	public void setScheduleDateTime(String scheduleDateTime) {
		this.scheduleDateTime = scheduleDateTime;
	}


	@Override
	public String toString() {
		return "DriverOrder [id=" + id + ", orderNumber=" + orderNumber + ", orderId=" + orderId + ", firstName="
				+ firstName + ", lastName=" + lastName + ", email=" + email + ", phone=" + phone + ", statusId="
				+ statusId + ", statusName=" + statusName + ", businessName=" + businessName + ", businessAddress="
				+ businessAddress + ", customerAddress=" + customerAddress + ", time=" + time + ", created=" + created
				+ ", createdBy=" + createdBy + ", updated=" + updated + ", updatedBy=" + updatedBy + ", nxpAccessToken="
				+ nxpAccessToken + ", businessId=" + businessId + ", locationsId=" + locationsId + ", accountId="
				+ accountId + ", driverId=" + driverId + ", scheduleDateTime=" + scheduleDateTime + "]";
	}
	
	

}
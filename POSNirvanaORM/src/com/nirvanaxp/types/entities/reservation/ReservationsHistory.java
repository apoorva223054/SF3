/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.reservation;

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
 * The persistent class for the reservations_history database table.
 * 
 */
@Entity
@Table(name = "reservations_history")
@XmlRootElement(name = "reservations_history")
public class ReservationsHistory implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private BigInteger id;

	@Column(length = 1024)
	private String comment;

	@Column(name = "contact_preference_1")
	private String contactPreference1;

	@Column(name = "contact_preference_2")
	private String contactPreference2;

	@Column(name = "contact_preference_3")
	private String contactPreference3;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(nullable = false, length = 10)
	private String date;

	@Column(length = 32)
	private String email;

	@Column(name = "first_name", length = 56)
	private String firstName;

	@Column(name = "last_name", length = 64)
	private String lastName;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(name = "party_size", nullable = false)
	private int partySize;

	@Column(name = "phone_number", length = 32)
	private String phoneNumber;

	@Column(name = "request_type_id")
	private String requestTypeId;

	@Column(name = "reservation_platform", nullable = false, length = 64)
	private String reservationPlatform;

	@Column(name = "reservation_source", nullable = false, length = 64)
	private String reservationSource;

	@Column(name = "reservation_types_id", nullable = false)
	private int reservationTypesId;

	@Column(name = "reservations_id")
	private String reservationsId;

	@Column(name = "reservations_status_id")
	private String reservationsStatusId;

	@Column(nullable = false, length = 8)
	private String time;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "users_id", nullable = false)
	private String usersId;

	@Column(name = "session_id")
	private Integer sessionKey;

	@Column(name = "pre_assigned_location_id", nullable = false)
	private int preAssignedLocationId;
	
	@Column(name = "is_order_present")
	private Integer isOrderPresent;
	
	@Column(name = "business_comment")
	private String businessComment ;
	
	@Column(name = "local_time")
	private String localTime;

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}

	public String getBusinessComment() {
		return businessComment;
	}

	public void setBusinessComment(String businessComment) {
		this.businessComment = businessComment;
	}

	public ReservationsHistory()
	{
	}

	 

	public BigInteger getId()
	{
		return id;
	}

	public void setId(BigInteger id)
	{
		this.id = id;
	}

	public String getComment()
	{
		return this.comment;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public String getContactPreference1()
	{
		 if(contactPreference1 != null && (contactPreference1.length()==0 || contactPreference1.equals("0"))){return null;}else{	return contactPreference1;}
	}

	public void setContactPreference1(String contactPreference1)
	{
		this.contactPreference1 = contactPreference1;
	}

	public String getContactPreference2()
	{
		 if(contactPreference2 != null && (contactPreference2.length()==0 || contactPreference2.equals("0"))){return null;}else{	return contactPreference2;}
	}

	public void setContactPreference2(String contactPreference2)
	{
		this.contactPreference2 = contactPreference2;
	}

	public String getContactPreference3()
	{
		 if(contactPreference3 != null && (contactPreference3.length()==0 || contactPreference3.equals("0"))){return null;}else{	return contactPreference3;}
	}

	public void setContactPreference3(String contactPreference3)
	{
		this.contactPreference3 = contactPreference3;
	}

	public String getDate()
	{
		return this.date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getEmail()
	{
		return this.email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getFirstName()
	{
		return this.firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return this.lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public int getPartySize()
	{
		return this.partySize;
	}

	public void setPartySize(int partySize)
	{
		this.partySize = partySize;
	}

	public String getPhoneNumber()
	{
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	public String getRequestTypeId()
	{
		 if(requestTypeId != null && (requestTypeId.length()==0 || requestTypeId.equals("0"))){return null;}else{	 if(requestTypeId != null && (requestTypeId.length()==0 || requestTypeId.equals("0"))){return null;}else{	return requestTypeId;}}
	}

	public void setRequestTypeId(String requestTypeId)
	{
		this.requestTypeId = requestTypeId;
	}

	public String getReservationPlatform()
	{
		return this.reservationPlatform;
	}

	public void setReservationPlatform(String reservationPlatform)
	{
		this.reservationPlatform = reservationPlatform;
	}

	public String getReservationSource()
	{
		return this.reservationSource;
	}

	public void setReservationSource(String reservationSource)
	{
		this.reservationSource = reservationSource;
	}

	public int getReservationTypesId()
	{
		return this.reservationTypesId;
	}

	public void setReservationTypesId(int reservationTypesId)
	{
		this.reservationTypesId = reservationTypesId;
	}

 

	public String getReservationsId()
	{
		if(reservationsId != null && (reservationsId.length()==0 || reservationsId.equals("0"))){return null;}else{	return reservationsId;}
	}

	public void setReservationsId(String reservationsId)
	{
		this.reservationsId = reservationsId;
	}

	public String getReservationsStatusId()
	{
		 if(reservationsStatusId != null && (reservationsStatusId.length()==0 || reservationsStatusId.equals("0"))){return null;}else{	return reservationsStatusId;}
	}

	public void setReservationsStatusId(String reservationsStatusId)
	{
		this.reservationsStatusId = reservationsStatusId;
	}

	public String getTime()
	{
		return this.time;
	}

	public void setTime(String time)
	{
		this.time = time;
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


	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	public Integer getSessionKey()
	{
		return sessionKey;
	}

	public void setSessionKey(Integer sessionKey)
	{
		this.sessionKey = sessionKey;
	}

	public int getPreAssignedLocationId()
	{
		return preAssignedLocationId;
	}

	public void setPreAssignedLocationId(int preAssignedLocationId)
	{
		this.preAssignedLocationId = preAssignedLocationId;
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
	public Integer getIsOrderPresent() {
		return isOrderPresent;
	}

	public void setIsOrderPresent(Integer isOrderPresent) {
		this.isOrderPresent = isOrderPresent;
	}

	@Override
	public String toString() {
		return "ReservationsHistory [id=" + id + ", comment=" + comment
				+ ", contactPreference1=" + contactPreference1
				+ ", contactPreference2=" + contactPreference2
				+ ", contactPreference3=" + contactPreference3 + ", created="
				+ created + ", createdBy=" + createdBy + ", date=" + date
				+ ", email=" + email + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", locationsId=" + locationsId
				+ ", partySize=" + partySize + ", phoneNumber=" + phoneNumber
				+ ", requestTypeId=" + requestTypeId + ", reservationPlatform="
				+ reservationPlatform + ", reservationSource="
				+ reservationSource + ", reservationTypesId="
				+ reservationTypesId + ", reservationsId=" + reservationsId
				+ ", reservationsStatusId=" + reservationsStatusId + ", time="
				+ time + ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", usersId=" + usersId + ", sessionKey=" + sessionKey
				+ ", preAssignedLocationId=" + preAssignedLocationId
				+ ", isOrderPresent=" + isOrderPresent + ", businessComment="
				+ businessComment +  ", localTime=" + localTime +"]";
	}

	


	

}
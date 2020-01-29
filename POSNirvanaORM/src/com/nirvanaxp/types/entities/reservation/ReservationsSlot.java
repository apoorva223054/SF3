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

@Entity
@Table(name = "reservation_slots")
@XmlRootElement(name = "reservation_slots")
public class ReservationsSlot implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2383163151390491812L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "status")
	private String status;

	@Column(name = "date")
	private String date;

	// start of the slot
	@Column(name = "slot_start_time", nullable = false)
	private String slotStartTime;

	@Column(name = "slot_end_time", nullable = false)
	private String slotEndTime;

	// time for the slot
	@Column(name = "slot_interval", nullable = false)
	private int slotTime;

	@Column(name = "reservation_schedule_id", nullable = false)
	private String reservationScheduleId;

	// no of clients that have requested to hol the reservation slot, but yet
	// have not done any reservation
	@Column(name = "currenlty_holded_client", nullable = false)
	private int currentlyHoldedClient;

	@Column(name = "current_reservation_in_slot", nullable = false)
	private int currentReservationInSlot;

	@Column(name = "location_id")
	private String locationId;

	@Column(name = "is_blocked")
	private int isBlocked;

	public ReservationsSlot()
	{
		super();
	}

	public ReservationsSlot(String createdBy, String updatedBy, String status, String slotStartTime, int slotTime, String reservationScheduleId, int currentlyHoldedClient, int currentReservationInSlot,
			String slotEndTime, String locationId, int isBlocked)
	{
		super();
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.status = status;
		this.slotStartTime = slotStartTime;
		this.slotTime = slotTime;
		this.reservationScheduleId = reservationScheduleId;
		this.currentlyHoldedClient = currentlyHoldedClient;
		this.currentReservationInSlot = currentReservationInSlot;
		this.slotEndTime = slotEndTime;
		this.locationId = locationId;
		this.isBlocked = isBlocked;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
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

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getSlotStartTime()
	{
		return slotStartTime;
	}

	public void setSlotStartTime(String slotStartTime)
	{
		this.slotStartTime = slotStartTime;
	}

	public String getSlotEndTime()
	{
		return slotEndTime;
	}

	public void setSlotEndTime(String slotEndTime)
	{
		this.slotEndTime = slotEndTime;
	}

	public int getSlotTime()
	{
		return slotTime;
	}

	public void setSlotTime(int slotTime)
	{
		this.slotTime = slotTime;
	}

	public String getReservationScheduleId() {
		 if(reservationScheduleId != null && (reservationScheduleId.length()==0 || reservationScheduleId.equals("0"))){return null;}else{	return reservationScheduleId;}
	}

	public void setReservationScheduleId(String reservationScheduleId) {
		this.reservationScheduleId = reservationScheduleId;
	}

	public int getCurrentlyHoldedClient()
	{
		return currentlyHoldedClient;
	}

	public void setCurrentlyHoldedClient(int currentlyHoldedClient)
	{
		this.currentlyHoldedClient = currentlyHoldedClient;
	}

	public int getCurrentReservationInSlot()
	{
		return currentReservationInSlot;
	}

	public void setCurrentReservationInSlot(int currentReservationInSlot)
	{
		this.currentReservationInSlot = currentReservationInSlot;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	/**
	 * @return the isBlocked
	 */
	public int getIsBlocked()
	{
		return isBlocked;
	}

	/**
	 * @param isBlocked
	 *            the isBlocked to set
	 */
	public void setIsBlocked(int isBlocked)
	{
		this.isBlocked = isBlocked;
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

	@Override
	public String toString() {
		return "ReservationsSlot [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", status=" + status + ", date="
				+ date + ", slotStartTime=" + slotStartTime + ", slotEndTime="
				+ slotEndTime + ", slotTime=" + slotTime
				+ ", reservationScheduleId=" + reservationScheduleId
				+ ", currentlyHoldedClient=" + currentlyHoldedClient
				+ ", currentReservationInSlot=" + currentReservationInSlot
				+ ", locationId=" + locationId + ", isBlocked=" + isBlocked
				+ "]";
	}

}

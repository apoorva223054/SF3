/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.reservation;

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
@Table(name = "reservation_slots_active_client_info")
@XmlRootElement(name = "reservation_slots_active_client_info")
public class ReservationSlotActiveClientInfo
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "session_id")
	private String sessionId;

	// start of the slot
	@Column(name = "slot_hold_start_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date slotHoldStartTime;

	@Column(name = "hold_time", nullable = false)
	private int holdTime;

	// time for the slot
	@Column(name = "reservation_slot_id", nullable = false)
	private int reservationSlotId;

	@Column(name = "is_reservtion_made_by_client")
	private boolean isReservtionMadeByClient;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "created_by")
	private String createdBy;

	public ReservationSlotActiveClientInfo()
	{
		super();
	}

	public ReservationSlotActiveClientInfo(String sessionId, int holdTime, int reservationSlotId)
	{
		this.sessionId = sessionId;
		this.holdTime = holdTime;
		this.reservationSlotId = reservationSlotId;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public int getHoldTime()
	{
		return holdTime;
	}

	public void setHoldTime(int holdTime)
	{
		this.holdTime = holdTime;
	}

	public int getReservationSlotId()
	{
		return reservationSlotId;
	}

	public void setReservationSlotId(int reservationSlotId)
	{
		this.reservationSlotId = reservationSlotId;
	}

	public Date getSlotHoldStartTime()
	{
		return slotHoldStartTime;
	}

	public void setSlotHoldStartTime(Date slotHoldStartTime)
	{
		this.slotHoldStartTime = slotHoldStartTime;
	}

	public boolean isReservtionMadeByClient()
	{
		return isReservtionMadeByClient;
	}

	public void setReservtionMadeByClient(boolean isReservtionMadeByClient)
	{
		this.isReservtionMadeByClient = isReservtionMadeByClient;
	}


	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
		return "ReservationSlotActiveClientInfo [id=" + id + ", created="
				+ created + ", updated=" + updated + ", sessionId=" + sessionId
				+ ", slotHoldStartTime=" + slotHoldStartTime + ", holdTime="
				+ holdTime + ", reservationSlotId=" + reservationSlotId
				+ ", isReservtionMadeByClient=" + isReservtionMadeByClient
				+ ", updatedBy=" + updatedBy + ", createdBy=" + createdBy + "]";
	}

}

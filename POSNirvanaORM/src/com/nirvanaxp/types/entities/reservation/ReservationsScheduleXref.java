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
 * The persistent class for the reservations_schedule_xref database table.
 * 
 */
@Entity
@Table(name = "reservations_schedule_xref")
@XmlRootElement(name = "reservations_schedule_xref")
public class ReservationsScheduleXref implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "from_time", nullable = false, length = 30)
	private String fromTime;

	@Column(nullable = false, length = 1)
	private String status;

	@Column(name = "reservations_schedule_id", nullable = false)
	private String reservationsScheduleId;

	@Column(name = "to_time", nullable = false, length = 30)
	private String toTime;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	public ReservationsScheduleXref()
	{
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getFromTime()
	{
		return this.fromTime;
	}

	public void setFromTime(String fromTime)
	{
		this.fromTime = fromTime;
	}

	public String getToTime()
	{
		return this.toTime;
	}

	public String getReservationsScheduleId()
	{
		 if(reservationsScheduleId != null && (reservationsScheduleId.length()==0 || reservationsScheduleId.equals("0"))){return null;}else{	return reservationsScheduleId;}
	}

	public void setReservationsScheduleId(String reservationsScheduleId)
	{
		this.reservationsScheduleId = reservationsScheduleId;
	}

	public void setToTime(String toTime)
	{
		this.toTime = toTime;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
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

	@Override
	public String toString() {
		return "ReservationsScheduleXref [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", fromTime=" + fromTime
				+ ", status=" + status + ", reservationsScheduleId="
				+ reservationsScheduleId + ", toTime=" + toTime + ", updated="
				+ updated + ", updatedBy=" + updatedBy + "]";
	}

}
/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.reservation;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The persistent class for the reservation_schedule database table.
 * 
 */
@Entity
@Table(name = "reservations_schedule")
@XmlRootElement(name = "reservations_schedule")
public class ReservationsSchedule implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "from_date", nullable = false, length = 30)
	private String fromDate;

	@Column(name = "from_time", nullable = false, length = 30)
	private String fromTime;

	@Column(name = "location_id", nullable = false)
	private String locationId;

	@Column(name = "shift_name", nullable = false, length = 250)
	private String shiftName;

	@Column(nullable = false, length = 1)
	private String status;

	@Column(name = "to_date", nullable = false, length = 30)
	private String toDate;

	@Column(name = "to_time", nullable = false, length = 30)
	private String toTime;

	@Column(name = "start_week", nullable = false)
	private int startWeek;

	@Column(name = "end_week", nullable = false)
	private int endWeek;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "is_reservations_allowed")
	private int isReservationsAllowed;

	@Column(name = "slot_time")
	private int slotTime;

	@Column(name = "reservation_allowed")
	private int reservationAllowed;

	// uni-directional many-to-many association to Discount
	@JoinColumn(name = "reservations_schedule_id")
	@OneToMany(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<ReservationsScheduleDay> reservationsScheduleDays;

	@JoinColumn(name = "reservations_schedule_id")
	@OneToMany(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<ReservationsScheduleXref> reservationsScheduleXref;

	@Column(name = "shift_group_id", nullable = false)
	private Integer shiftGroupId;

	public ReservationsSchedule()
	{
	}

	public ReservationsSchedule(Object[] obj)
	{
		// the passed object[] must have data in this Item
		setFromDate((String) obj[0]);
		setId((String) obj[1]);
		setFromTime((String) obj[2]);
		setToTime((String) obj[3]);
		setToDate((String) obj[4]);
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

	public String getFromDate()
	{
		return this.fromDate;
	}

	public void setFromDate(String fromDate)
	{
		this.fromDate = fromDate;
	}

	public String getFromTime()
	{
		return this.fromTime;
	}

	public void setFromTime(String fromTime)
	{
		this.fromTime = fromTime;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public String getShiftName()
	{
		return this.shiftName;
	}

	public void setShiftName(String shiftName)
	{
		this.shiftName = shiftName;
	}

	public String getToDate()
	{
		return this.toDate;
	}

	public void setToDate(String toDate)
	{
		this.toDate = toDate;
	}

	public String getToTime()
	{
		return this.toTime;
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

	public int getStartWeek()
	{
		return startWeek;
	}

	public void setStartWeek(int startWeek)
	{
		this.startWeek = startWeek;
	}

	public int getEndWeek()
	{
		return endWeek;
	}

	public void setEndWeek(int endWeek)
	{
		this.endWeek = endWeek;
	}

	public int getIsReservationsAllowed()
	{
		return isReservationsAllowed;
	}

	public void setIsReservationsAllowed(int isReservationsAllowed)
	{
		this.isReservationsAllowed = isReservationsAllowed;
	}

	public Set<ReservationsScheduleXref> getReservationsScheduleXref()
	{
		return reservationsScheduleXref;
	}

	public void setReservationsScheduleXref(Set<ReservationsScheduleXref> reservationsScheduleXref)
	{
		this.reservationsScheduleXref = reservationsScheduleXref;
	}

	public Integer getShiftGroupId()
	{
		return shiftGroupId;
	}

	public void setShiftGroupId(Integer shiftGroupId)
	{
		this.shiftGroupId = shiftGroupId;
	}

	public Set<ReservationsScheduleDay> getReservationsScheduleDays()
	{
		return reservationsScheduleDays;
	}

	public void setReservationsScheduleDays(Set<ReservationsScheduleDay> reservationsScheduleDays)
	{
		this.reservationsScheduleDays = reservationsScheduleDays;
	}

	public int getSlotTime()
	{
		return slotTime;
	}

	public void setSlotTime(int slotTime)
	{
		this.slotTime = slotTime;
	}

	public int getReservationAllowed()
	{
		return reservationAllowed;
	}

	public void setReservationAllowed(int reservationAllowed)
	{
		this.reservationAllowed = reservationAllowed;
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
		return "ReservationsSchedule [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", fromDate=" + fromDate
				+ ", fromTime=" + fromTime + ", locationId=" + locationId
				+ ", shiftName=" + shiftName + ", status=" + status
				+ ", toDate=" + toDate + ", toTime=" + toTime + ", startWeek="
				+ startWeek + ", endWeek=" + endWeek + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", isReservationsAllowed="
				+ isReservationsAllowed + ", slotTime=" + slotTime
				+ ", reservationAllowed=" + reservationAllowed
				+ ", reservationsScheduleDays=" + reservationsScheduleDays
				+ ", reservationsScheduleXref=" + reservationsScheduleXref
				+ ", shiftGroupId=" + shiftGroupId + "]";
	}

}
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

@Entity
@Table(name = "shift_schedule")
@XmlRootElement(name = "shift_schedule")
public class ShiftSchedule implements Serializable
{
 private static final long serialVersionUID = 1L;


 @Id
 @Column(unique = true, nullable = false)
 private String id;

 @Column(name = "shift_name", nullable = false, length = 250)
 private String shiftName;
 
 @Column(name = "from_time", nullable = false, length = 30)
 private String fromTime;
 
 @Column(name = "to_time", nullable = false, length = 30)
 private String toTime;
 
 @Column(name = "from_date", nullable = false, length = 30)
 private String fromDate;
 
 @Column(name = "to_date", nullable = false, length = 30)
 private String toDate;

 @Column(name = "location_id", nullable = false)
 private String locationId;
 
 @Column(name = "slot_time")
 private int slotTime;

 @Column(name = "max_order_allowed")
 private int maxOrderAllowed;
 
 @Column(name = "hold_time")
 private int holdTime;
 
 @Column(nullable = false, length = 1)
 private String status;
 
 @Temporal(TemporalType.TIMESTAMP)
 private Date created;

 @Column(name = "created_by")
 private String createdBy;

 @Temporal(TemporalType.TIMESTAMP)
 private Date updated;

 @Column(name = "updated_by")
 private String updatedBy;

 @Column(name = "order_source_group_id", nullable = false)
 private String orderSourceGroupId;
 

 public ShiftSchedule()
 {
 }

 public ShiftSchedule(Object[] obj)
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

public String getShiftName() {
  return shiftName;
 }

 public void setShiftName(String shiftName) {
  this.shiftName = shiftName;
 }

 public String getFromTime() {
  return fromTime;
 }

 public void setFromTime(String fromTime) {
  this.fromTime = fromTime;
 }

 public String getToTime() {
  return toTime;
 }

 public void setToTime(String toTime) {
  this.toTime = toTime;
 }

 public String getFromDate() {
  return fromDate;
 }

 public void setFromDate(String fromDate) {
  this.fromDate = fromDate;
 }

 public String getToDate() {
  return toDate;
 }

 public void setToDate(String toDate) {
  this.toDate = toDate;
 }

 public String getLocationId() {
  if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
 }

 public void setLocationId(String locationId) {
  this.locationId = locationId;
 }

 public int getSlotTime() {
  return slotTime;
 }

 public void setSlotTime(int slotTime) {
  this.slotTime = slotTime;
 }

 public int getMaxOrderAllowed() {
  return maxOrderAllowed;
 }

 public void setMaxOrderAllowed(int maxOrderAllowed) {
  this.maxOrderAllowed = maxOrderAllowed;
 }

 public int getHoldTime() {
  return holdTime;
 }

 public void setHoldTime(int holdTime) {
  this.holdTime = holdTime;
 }

 public String getStatus() {
  return status;
 }

 public void setStatus(String status) {
  this.status = status;
 }

 public Date getCreated() {
  return created;
 }

 public void setCreated(Date created) {
  this.created = created;
 }

 public Date getUpdated() {
  return updated;
 }

 public void setUpdated(Date updated) {
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

public String getOrderSourceGroupId() {
   if(orderSourceGroupId != null && (orderSourceGroupId.length()==0 || orderSourceGroupId.equals("0"))){return null;}else{	return orderSourceGroupId;}
 }

 public void setOrderSourceGroupId(String orderSourceGroupId) {
  this.orderSourceGroupId = orderSourceGroupId;
 }

 @Override
 public String toString() {
  return "ShiftSchedule [id=" + id + ", shiftName=" + shiftName
    + ", fromTime=" + fromTime + ", toTime=" + toTime
    + ", fromDate=" + fromDate + ", toDate=" + toDate
    + ", locationId=" + locationId + ", slotTime=" + slotTime
    + ", maxOrderAllowed=" + maxOrderAllowed + ", holdTime="
    + holdTime + ", status=" + status + ", created=" + created
    + ", createdBy=" + createdBy + ", updated=" + updated
    + ", updatedBy=" + updatedBy + ", orderSourceGroupId="
    + orderSourceGroupId + "]";
 }





}
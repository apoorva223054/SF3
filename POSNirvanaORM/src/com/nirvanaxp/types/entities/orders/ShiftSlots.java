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
@Table(name = "shift_slots")
@XmlRootElement(name = "shift_slots")
public class ShiftSlots implements Serializable
{

 /**
  * 
  */
 private static final long serialVersionUID = 2383163151390491812L;

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(unique = true, nullable = false)
 private int id;
 
 @Column(name = "shift_schedule_id", nullable = false)
 private String shiftScheduleId;
 
 @Column(name = "date")
 private String date;

 @Column(name = "slot_time", nullable = false)
 private String slotTime;

 // time for the slot
 @Column(name = "slot_interval", nullable = false)
 private int slotInterval;
 
 

 @Column(name = "currenlty_holded_client", nullable = false)
 private int currentlyHoldedClient;

 @Column(name = "current_order_in_slot", nullable = false)
 private int currentOrderInSlot;
 
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

 @Column(name = "location_id")
 private String locationId;

 @Column(name = "is_blocked")
 private int isBlocked;
 

 public ShiftSlots()
 {
  super();
 }
 
 

 public ShiftSlots(int id, String shiftScheduleId, String date, String slotTime, int slotInterval,
		int currentlyHoldedClient, int currentOrderInSlot, Date created, String createdBy, Date updated, String updatedBy,
		String status, String locationId, int isBlocked) {
	super();
	this.id = id;
	this.shiftScheduleId = shiftScheduleId;
	this.date = date;
	this.slotTime = slotTime;
	this.slotInterval = slotInterval;
	this.currentlyHoldedClient = currentlyHoldedClient;
	this.currentOrderInSlot = currentOrderInSlot;
	this.created = created;
	this.createdBy = createdBy;
	this.updated = updated;
	this.updatedBy = updatedBy;
	this.status = status;
	this.locationId = locationId;
	this.isBlocked = isBlocked;
}



public int getId() {
	return id;
}



public void setId(int id) {
	this.id = id;
}



public String getShiftScheduleId() {
	 if(shiftScheduleId != null && (shiftScheduleId.length()==0 || shiftScheduleId.equals("0"))){return null;}else{	return shiftScheduleId;}
}



public void setShiftScheduleId(String shiftScheduleId) {
	this.shiftScheduleId = shiftScheduleId;
}



public String getDate() {
	return date;
}



public void setDate(String date) {
	this.date = date;
}



public String getSlotTime() {
	return slotTime;
}



public void setSlotTime(String slotTime) {
	this.slotTime = slotTime;
}



public int getSlotInterval() {
	return slotInterval;
}



public void setSlotInterval(int slotInterval) {
	this.slotInterval = slotInterval;
}



public int getCurrentlyHoldedClient() {
	return currentlyHoldedClient;
}



public void setCurrentlyHoldedClient(int currentlyHoldedClient) {
	this.currentlyHoldedClient = currentlyHoldedClient;
}



public int getCurrentOrderInSlot() {
	return currentOrderInSlot;
}



public void setCurrentOrderInSlot(int currentOrderInSlot) {
	this.currentOrderInSlot = currentOrderInSlot;
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



public String getStatus() {
	return status;
}



public void setStatus(String status) {
	this.status = status;
}



public String getLocationId() {
	if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
}



public void setLocationId(String locationId) {
	this.locationId = locationId;
}



public int getIsBlocked() {
	return isBlocked;
}



public void setIsBlocked(int isBlocked) {
	this.isBlocked = isBlocked;
}



public static long getSerialversionuid() {
	return serialVersionUID;
}



@Override
public String toString() {
	return "ShiftSlots [id=" + id + ", shiftScheduleId=" + shiftScheduleId + ", date=" + date + ", slotTime=" + slotTime
			+ ", slotInterval=" + slotInterval + ", currentlyHoldedClient=" + currentlyHoldedClient
			+ ", currentOrderInSlot=" + currentOrderInSlot + ", created=" + created + ", createdBy=" + createdBy
			+ ", updated=" + updated + ", updatedBy=" + updatedBy + ", status=" + status + ", locationId=" + locationId
			+ ", isBlocked=" + isBlocked + "]";
}



}
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
@Table(name = "order_source_group_to_shift_schedule")
@XmlRootElement(name = "order_source_group_to_shift_schedule")
public class OrderSourceGroupToShiftSchedule implements Serializable
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
	 
	 @Column(name = "order_source_group_id", nullable = false)
	 private String orderSourceGroupId;
	 
	 @Column(name = "from_time", nullable = false, length = 30)
	 private String fromTime;
	 
	 @Column(name = "to_time", nullable = false, length = 30)
	 private String toTime;
	 
	 @Column(name = "from_date", nullable = false, length = 30)
	 private String fromDate;
	 
	 @Column(name = "to_date", nullable = false, length = 30)
	 private String toDate;

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

	public OrderSourceGroupToShiftSchedule() {
		super();
		
	}

	public OrderSourceGroupToShiftSchedule(int id, String shiftScheduleId, String orderSourceGroupId, String fromTime,
			String toTime, String fromDate, String toDate, Date created, String createdBy, Date updated, String updatedBy,
			String status) {
		super();
		this.id = id;
		this.shiftScheduleId = shiftScheduleId;
		this.orderSourceGroupId = orderSourceGroupId;
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.created = created;
		this.createdBy = createdBy;
		this.updated = updated;
		this.updatedBy = updatedBy;
		this.status = status;
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

	public String getOrderSourceGroupId() {
		 if(orderSourceGroupId != null && (orderSourceGroupId.length()==0 || orderSourceGroupId.equals("0"))){return null;}else{	return orderSourceGroupId;}
	}

	public void setOrderSourceGroupId(String orderSourceGroupId) {
		this.orderSourceGroupId = orderSourceGroupId;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "OrderSourceGroupToShiftSchedule [id=" + id + ", shiftScheduleId=" + shiftScheduleId
				+ ", orderSourceGroupId=" + orderSourceGroupId + ", fromTime=" + fromTime + ", toTime=" + toTime
				+ ", fromDate=" + fromDate + ", toDate=" + toDate + ", created=" + created + ", createdBy=" + createdBy
				+ ", updated=" + updated + ", updatedBy=" + updatedBy + ", status=" + status + "]";
	}

	
	 


}

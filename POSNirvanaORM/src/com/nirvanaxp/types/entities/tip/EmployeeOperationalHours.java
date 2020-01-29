package com.nirvanaxp.types.entities.tip;

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
@Table(name = "employee_operational_hours")
@XmlRootElement(name = "employee_operational_hours")
public class EmployeeOperationalHours implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "number_of_hours", nullable = false, length = 30)
	private String numberOfHours;

	@Column(name = "location_id", nullable = false)
	private String locationId;

	@Column(name = "employee_id")
	private int employeeId;

	@Column(name = "shift_id")
	private String shiftId;

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

	@Column(name = "local_time")
	private String localTime;
	
	@Column(name = "total_shift_hr")
	private String totalShiftHr;

	@Column(name = "nirvanaxp_batch_id")
	int nirvanaxpBatchId;
	
	@Column(name = "job_role_id")
	String jobRoleId;
	
	
	public int getNirvanaxpBatchId()
	{
		return nirvanaxpBatchId;
	}



	public void setNirvanaxpBatchId(int nirvanaxpBatchId)
	{
		this.nirvanaxpBatchId = nirvanaxpBatchId;
	}
	
	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getNumberOfHours()
	{
		return numberOfHours;
	}

	public void setNumberOfHours(String numberOfHours)
	{
		this.numberOfHours = numberOfHours;
	}

	public String getLocationId()
	{
		if(locationId != null && (locationId.length()==0 || locationId.equals("0"))){return null;}else{	return locationId;}
	}

	public void setLocationId(String locationId)
	{
		this.locationId = locationId;
	}

	public int getEmployeeId()
	{
		return employeeId;
	}

	public void setEmployeeId(int employeeId)
	{
		this.employeeId = employeeId;
	}

	public String getShiftId()
	{
		 if(shiftId != null && (shiftId.length()==0 || shiftId.equals("0"))){return null;}else{	return shiftId;}
	}

	public void setShiftId(String shiftId)
	{
		this.shiftId = shiftId;
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

	
	public String getTotalShiftHr() {
		return totalShiftHr;
	}



	public void setTotalShiftHr(String totalShiftHr) {
		this.totalShiftHr = totalShiftHr;
	}



	public EmployeeOperationalHours()
	{
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



	public String getJobRoleId()
	{
		 if(jobRoleId != null && (jobRoleId.length()==0 || jobRoleId.equals("0"))){return null;}else{	return jobRoleId;}
	}



	public void setJobRoleId(String jobRoleId)
	{
		this.jobRoleId = jobRoleId;
	}



	@Override
	public String toString()
	{
		return "EmployeeOperationalHours [id=" + id + ", numberOfHours=" + numberOfHours + ", locationId=" + locationId + ", employeeId=" + employeeId + ", shiftId=" + shiftId + ", status=" + status
				+ ", created=" + created + ", createdBy=" + createdBy + ", updated=" + updated + ", updatedBy=" + updatedBy + ", localTime=" + localTime + ", totalShiftHr=" + totalShiftHr
				+ ", nirvanaxpBatchId=" + nirvanaxpBatchId + ", jobRoleId=" + jobRoleId + "]";
	}




	
	

}
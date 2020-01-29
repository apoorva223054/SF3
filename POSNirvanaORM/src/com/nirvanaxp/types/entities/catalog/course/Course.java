/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.catalog.course;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.locations.Location;


/**
 * The persistent class for the course database table.
 * 
 */
@Entity
@Table(name = "course")
@XmlRootElement(name = "course")
public class Course implements Serializable
{


	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String  id;

	@Column(name = "course_name", nullable = false, length = 64)
	private String courseName;

	@Column(name = "image_name")
	private String imageName;

	@Column(name = "hex_code_values")
	private String hexCodeValues;

	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "display_name", length = 64)
	private String displayName;

	@Column(name = "display_sequence")
	private Integer displaySequence;

	@Column(name = "is_active", nullable = false)
	private int isActive;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(nullable = false, length = 1)
	private String status;

	@Column(name = "global_course_id")
	private String globalCourseId;

	private transient List<Location> locationList;
	
	public Course()
	{
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
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

	public String getCourseName()
	{
		return this.courseName;
	}

	public void setCourseName(String courseName)
	{
		this.courseName = courseName;
	}


	public String getDisplayName()
	{
		return this.displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public Integer getDisplaySequence()
	{
		return this.displaySequence;
	}

	public void setDisplaySequence(Integer displaySequence)
	{
		this.displaySequence = displaySequence;
	}

	public int getIsActive()
	{
		return this.isActive;
	}

	public void setIsActive(int isActive)
	{
		this.isActive = isActive;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getImageName()
	{
		return imageName;
	}

	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}

	public String getHexCodeValues()
	{
		return hexCodeValues;
	}

	public void setHexCodeValues(String hexCodeValues)
	{
		this.hexCodeValues = hexCodeValues;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
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

	public String getGlobalCourseId() {
		return globalCourseId;
	}

	public void setGlobalCourseId(String globalCourseId) {
		this.globalCourseId = globalCourseId;
	}

	public List<Location> getLocationList() {
		return locationList;
	}

	public void setLocationList(List<Location> locationList) {
		this.locationList = locationList;
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
		return "Course [id=" + id + ", courseName=" + courseName
				+ ", imageName=" + imageName + ", hexCodeValues="
				+ hexCodeValues + ", description=" + description + ", created="
				+ created + ", createdBy=" + createdBy + ", displayName="
				+ displayName + ", displaySequence=" + displaySequence
				+ ", isActive=" + isActive + ", locationsId=" + locationsId
				+ ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", status=" + status + ", globalCourseId=" + globalCourseId
				+ "]";
	}
	
	
	public Course getCourse(Course course){
		Course c = new Course();
		c.setCourseName(course.getCourseName());
		c.setCreated(course.getCreated());
		c.setCreatedBy(course.getCreatedBy());
		c.setDescription(course.getDescription());
		c.setDisplayName(course.getDisplayName());
		c.setDisplaySequence(course.getDisplaySequence());
		c.setGlobalCourseId(course.getGlobalCourseId());
		c.setHexCodeValues(course.getHexCodeValues());
		c.setImageName(course.getImageName());
		c.setIsActive(course.getIsActive());
		c.setLocationsId(course.getLocationsId());
		c.setStatus(course.getStatus());
		c.setUpdated(course.getUpdated());
		c.setUpdatedBy(course.getUpdatedBy());
		return c;
	}

		
}
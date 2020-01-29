/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.feedback;

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
 * The persistent class for the smileys database table.
 * 
 */
@Entity
@Table(name = "smileys")
@XmlRootElement(name = "smileys")
public class Smiley implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "feedback_type_id")
	private int feedbackTypeId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "simley_name", nullable = false, length = 32)
	private String simleyName;

	@Column(name = "image_name", nullable = false, length = 32)
	private String imageName;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@Column(name = "star_value")
	private int starValue;

	public Smiley()
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

 	public int getFeedbackTypeId() {
		return feedbackTypeId;
	}

	public void setFeedbackTypeId(int feedbackTypeId) {
		this.feedbackTypeId = feedbackTypeId;
	}

	public String getSimleyName()
	{
		return this.simleyName;
	}

	public void setSimleyName(String simleyName)
	{
		this.simleyName = simleyName;
	}

	public String getImageName()
	{
		return this.imageName;
	}

	public void setImageName(String imageName)
	{
		this.imageName = imageName;
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

	public int getStarValue()
	{
		return starValue;
	}

	public void setStarValue(int starValue)
	{
		this.starValue = starValue;
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
		return "Smiley [id=" + id + ", feedbackTypeId=" + feedbackTypeId
				+ ", created=" + created + ", createdBy=" + createdBy
				+ ", simleyName=" + simleyName + ", imageName=" + imageName
				+ ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", starValue=" + starValue + "]";
	}

}
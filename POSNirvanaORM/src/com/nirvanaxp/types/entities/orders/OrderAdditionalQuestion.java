package com.nirvanaxp.types.entities.orders;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "order_additional_question")
@XmlRootElement(name = "order_additional_question")
public class OrderAdditionalQuestion implements Serializable
{
	private static final long serialVersionUID = 1L;

//	 id, question, field_type_id, created,created_by, updated, updated_by
	
	@Id
	@Column(unique = true, nullable = false)
	private String id;
	
	@Column(name = "question")
	private String question;
	
	@Column(name = "field_type_id")
	private int fieldTypeId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "location_id")
	private String locationId;
	
	@Column(name = "display_sequence")
	private int displaySequence;

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

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getFieldTypeId() {
		return fieldTypeId;
	}

	public void setFieldTypeId(int fieldTypeId) {
		this.fieldTypeId = fieldTypeId;
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

	public int getDisplaySequence() {
		return displaySequence;
	}

	public void setDisplaySequence(int displaySequence) {
		this.displaySequence = displaySequence;
	}

	@Override
	public String toString() {
		return "OrderAdditionalQuestion [id=" + id + ", question=" + question + ", fieldTypeId=" + fieldTypeId
				+ ", created=" + created + ", createdBy=" + createdBy + ", updated=" + updated + ", updatedBy="
				+ updatedBy + ", status=" + status + ", locationId=" + locationId + ", displaySequence="
				+ displaySequence + "]";
	}

	
	
	
}

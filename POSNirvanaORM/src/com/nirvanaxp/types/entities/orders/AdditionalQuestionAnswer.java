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
@Table(name = "additional_question_answer")
@XmlRootElement(name = "additional_question_answer")
public class AdditionalQuestionAnswer implements Serializable
{
	private static final long serialVersionUID = 1L;

//	 id, question, field_type_id, created,created_by, updated, updated_by
	
	@Id
	@Column(unique = true, nullable = false)
	private String id;
	
	@Column(name = "order_header_id")
	private String orderHeaderId;
	
	@Column(name = "question_id")
	private int questionId;
	
	@Column(name = "answer_value")
	private String answerValue;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;
	
	@Column(name = "display_sequence")
	private int displaySequence;

	public String getId() {
		if(id != null && (id.length()==0 || id.equals("0"))){
			return null;
		}else{
			return id;
		}
	 
	}
	public void setId(String id) {
		this.id = id;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public String getAnswerValue() {
		return answerValue;
	}

	public void setAnswerValue(String answerValue) {
		this.answerValue = answerValue;
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


	public String getOrderHeaderId() {
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}

	public void setOrderHeaderId(String orderHeaderId) {
		this.orderHeaderId = orderHeaderId;
	}

	public int getDisplaySequence() {
		return displaySequence;
	}

	public void setDisplaySequence(int displaySequence) {
		this.displaySequence = displaySequence;
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
		return "AdditionalQuestionAnswer [id=" + id + ", orderHeaderId="
				+ orderHeaderId + ", questionId=" + questionId
				+ ", answerValue=" + answerValue + ", created=" + created
				+ ", createdBy=" + createdBy + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", displaySequence="
				+ displaySequence + "]";
	}

	
	
}
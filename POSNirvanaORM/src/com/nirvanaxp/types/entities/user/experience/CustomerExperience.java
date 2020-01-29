/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.user.experience;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.types.entities.feedback.FeedbackQuestion;
import com.nirvanaxp.types.entities.feedback.Smiley;

/**
 * The persistent class for the customer_experience database table.
 * 
 */
@Entity
@Table(name = "customer_experience")
@XmlRootElement(name = "customer_experience")
public class CustomerExperience implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Column(nullable = false, length = 1200)
	private String comments;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "users_id", nullable = false)
	private String usersId;

	@Column(name = "locations_id", nullable = false)
	private String locationsId;

	@Column(name = "order_header_id", nullable = false)
	private String orderHeaderId;

	@Column(name = "updated_by", nullable = false)
	private String updatedBy;

	@ManyToOne
	@JoinColumn(name = "feedback_question_id")
	private FeedbackQuestion feedbackQuestion;

	@ManyToOne
	@JoinColumn(name = "smiley_id")
	private Smiley smiley;
	
	@Column(name = "local_time")
	private String localTime;

	public CustomerExperience()
	{
	}

	public String getLocalTime()
	{
		return localTime;
	}

	public void setLocalTime(String localTime)
	{
		this.localTime = localTime;
	}
	
 

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

	public String getComments()
	{
		return this.comments;
	}

	public void setComments(String comments)
	{
		this.comments = comments;
	}

	public long getCreated()
	{
		if (this.created != null)
		{
			return this.created.getTime();
		}
		return 0;
	}

	public void setCreated(long created)
	{
		if (created != 0)
		{
			this.created = new Date(created);
		}

	}

	public Date getUpdated()
	{
		return updated;

	}

	/*
	 * public int getFeedbackQuestionId() { return this.feedbackQuestionId; }
	 * 
	 * public void setFeedbackQuestionId(int feedbackQuestionId) {
	 * this.feedbackQuestionId = feedbackQuestionId; }
	 */

	
	public void setUpdated(long updated)
	{
		if (updated != 0)
		{
			this.updated = new Date(updated);
		}
	}

	public String getOrderHeaderId() {
		 if(orderHeaderId != null && (orderHeaderId.length()==0 || orderHeaderId.equals("0"))){return null;}else{	return orderHeaderId;}
	}

	public void setOrderHeaderId(String orderHeaderId) {
		this.orderHeaderId = orderHeaderId;
	}

	/*
	 * public int getSmileyId() { return smileyId; }
	 * 
	 * public void setSmileyId(int smileyId) { this.smileyId = smileyId; }
	 */

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


	public String getUsersId() {
		 if(usersId != null && (usersId.length()==0 || usersId.equals("0"))){return null;}else{	return usersId;}
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public FeedbackQuestion getFeedbackQuestion()
	{
		return feedbackQuestion;
	}

	public void setFeedbackQuestion(FeedbackQuestion feedbackQuestion)
	{
		this.feedbackQuestion = feedbackQuestion;
	}

	public Smiley getSmiley()
	{
		return smiley;
	}

	public void setSmiley(Smiley smiley)
	{
		this.smiley = smiley;
	}

	public CustomerExperience(ResultSet rs) throws SQLException
	{
		FeedbackQuestion feedbackQuestion = null;
		Smiley smiley = null;

		if (rs.getInt(1) != 0)
		{
			this.setId(rs.getString(1));
		}
		if (rs.getInt(2) != 0)
		{
			this.setUsersId((rs.getString(2)));
		}
		if (rs.getInt(3) != 0)
		{
			this.setLocationsId(rs.getString(3));
		}
		if (rs.getInt(4) != 0)
		{
			this.setOrderHeaderId(rs.getString(4));
		}
		// if(rs.getInt(5)!= 0){
		// experience.setFeedbackQuestionId(rs.getInt(5));
		// feedbackQuestion = new FeedbackQuestion();
		// }
		// if(rs.getInt(6)!= 0){
		// experience.setSmileyId(rs.getInt(6));
		// }
		if (rs.getString(7) != null)
		{
			this.setComments(rs.getString(7));
		}
		if (rs.getTimestamp(8) != null)
		{
			this.setCreated(rs.getTimestamp(8).getTime());
		}
		if (rs.getInt(9) != 0)
		{
			this.setCreatedBy((rs.getString(9)));
		}
		if (rs.getTimestamp(10) != null)
		{
			this.setUpdated(rs.getTimestamp(10).getTime());
		}
		if (rs.getInt(11) != 0)
		{
			this.setUpdatedBy((rs.getString(11)));
		}
		if (rs.getInt(5) != 0)
		{
			if (rs.getInt(12) != 0)
			{
				feedbackQuestion = new FeedbackQuestion();
				feedbackQuestion.setId(rs.getString(12));
			}
			if (rs.getString(13) != null)
			{
				feedbackQuestion.setFeedbackQuestion(rs.getString(13));
			}
			if (rs.getInt(14) != 0)
			{
				feedbackQuestion.setFeedbackTypeId(rs.getInt(14));
			}
			if (rs.getString(15) != null)
			{
				feedbackQuestion.setStatus(rs.getString(15));
			}
			if (rs.getInt(16) != 0)
			{
				feedbackQuestion.setDisplaySequence(rs.getInt(16));
			}
			if (rs.getInt(17) != 0)
			{
				feedbackQuestion.setLocationsId(rs.getString(17));
			}
			if (rs.getTimestamp(18) != null)
			{
				feedbackQuestion.setCreated(new Date(rs.getTimestamp(18).getTime()));
			}
			if (rs.getInt(19) != 0)
			{
				feedbackQuestion.setCreatedBy((rs.getString(19)));
			}
			if (rs.getTimestamp(20) != null)
			{
				feedbackQuestion.setUpdated(new Date(rs.getTimestamp(20).getTime()));
			}
			if (rs.getInt(21) != 0)
			{
				feedbackQuestion.setUpdatedBy((rs.getString(21)));
			}
		}
		this.setFeedbackQuestion(feedbackQuestion);
		if (rs.getInt(6) != 0)
		{
			smiley = new Smiley();
			if (rs.getInt(22) != 0)
			{
				smiley.setId(rs.getInt(22));
			}
			if (rs.getString(23) != null)
			{
				smiley.setSimleyName(rs.getString(23));
			}
			if (rs.getString(24) != null)
			{
				smiley.setImageName(rs.getString(24));
			}
			if (rs.getInt(25) != 0)
			{
				smiley.setFeedbackTypeId(rs.getInt(25));
			}
			if (rs.getString(26) != null)
			{
				smiley.setSimleyName(rs.getString(26));
			}
			if (rs.getTimestamp(27) != null)
			{
				smiley.setCreated(new Date(rs.getTimestamp(27).getTime()));
			}
			if (rs.getInt(28) != 0)
			{
				smiley.setCreatedBy((rs.getString(28)));
			}
			if (rs.getTimestamp(29) != null)
			{
				smiley.setUpdated(new Date(rs.getTimestamp(29).getTime()));
			}
			if (rs.getInt(30) != 0)
			{
				smiley.setUpdatedBy((rs.getString(30)));
			}
		}
		this.setSmiley(smiley);
		
		if (rs.getString(31) != null)
		{
			this.setLocalTime(rs.getString(31));
		}

	}

	public CustomerExperience(Object[] objRow)
	{

		FeedbackQuestion feedbackQuestion = null;
		Smiley smiley = null;

		if (objRow[0] != null)
		{
			this.setId((String) objRow[0]);
		}
		if (objRow[1] != null)
		{
			this.setUsersId((String) objRow[1]);
		}
		if (objRow[2] != null)
		{
			this.setLocationsId((String) objRow[2]);
		}
		if (objRow[3] != null)
		{
			this.setOrderHeaderId((String) objRow[3]);
		}
		// if(rs.getInt(5)!= 0){
		// experience.setFeedbackQuestionId(rs.getInt(5));
		// feedbackQuestion = new FeedbackQuestion();
		// }
		// if(rs.getInt(6)!= 0){
		// experience.setSmileyId(rs.getInt(6));
		// }
		if (objRow[6] != null)
		{
			this.setComments((String) objRow[6]);
		}
		if (objRow[7] != null)
		{
			this.setCreated(((Timestamp) objRow[7]).getTime());
		}
		if (objRow[8] != null)
		{
			this.setCreatedBy((String) objRow[8]);
		}
		if (objRow[9] != null)
		{
			this.setUpdated(((Timestamp) objRow[9]).getTime());
		}
		if (objRow[10] != null)
		{
			this.setUpdatedBy((String) objRow[10]);
		}
		if (objRow[4] != null)
		{
			if (objRow[11] != null)
			{
				feedbackQuestion = new FeedbackQuestion();
				feedbackQuestion.setId((String) objRow[11]);
			}
			if (objRow[12] != null)
			{
				feedbackQuestion.setFeedbackQuestion((String) objRow[12]);
			}
			if (objRow[13] != null)
			{
				feedbackQuestion.setFeedbackTypeId((int) objRow[13]);
			}
			if (objRow[14] != null)
			{
				feedbackQuestion.setStatus("" + (Character) objRow[14]);
			}
			if (objRow[15] != null)
			{
				feedbackQuestion.setDisplaySequence((Integer) objRow[15]);
			}
			if (objRow[16] != null)
			{
				feedbackQuestion.setLocationsId((String) objRow[16]);
			}
			if (objRow[17] != null)
			{
				feedbackQuestion.setCreated(new Date(((Timestamp) objRow[17]).getTime()));
			}
			if (objRow[18] != null)
			{
				feedbackQuestion.setCreatedBy((String) objRow[18]);
			}
			if (objRow[19] != null)
			{
				feedbackQuestion.setUpdated(new Date(((Timestamp) objRow[19]).getTime()));
			}
			if (objRow[20] != null)
			{
				feedbackQuestion.setUpdatedBy((String) objRow[20]);
			}
		}
		this.setFeedbackQuestion(feedbackQuestion);
		if (objRow[5] != null)
		{
			smiley = new Smiley();
			if (objRow[21] != null)
			{
				smiley.setId((Integer) objRow[21]);
			}
			if (objRow[22] != null)
			{
				smiley.setSimleyName((String) objRow[22]);
			}
			if (objRow[23] != null)
			{
				smiley.setImageName((String) objRow[23]);
			}
			if (objRow[24] != null)
			{
				smiley.setFeedbackTypeId((int) objRow[24]);
			}
			if (objRow[25] != null)
			{
				smiley.setStarValue((Integer) objRow[25]);
			}
			if (objRow[26] != null)
			{
				smiley.setCreated(new Date(((Timestamp) objRow[26]).getTime()));
			}
			if (objRow[27] != null)
			{
				smiley.setCreatedBy((String) objRow[27]);
			}
			if (objRow[28] != null)
			{
				smiley.setUpdated(new Date(((Timestamp) objRow[28]).getTime()));
			}
			if (objRow[29] != null)
			{
				smiley.setUpdatedBy((String) objRow[29]);
			}
		}
		this.setSmiley(smiley);
		
		if (objRow[30] != null)
		{
			this.setLocalTime((String)objRow[30]);
		}
	}

	@Override
	public String toString() {
		return "CustomerExperience [id=" + id + ", comments=" + comments
				+ ", created=" + created + ", createdBy=" + createdBy
				+ ", updated=" + updated + ", usersId=" + usersId
				+ ", locationsId=" + locationsId + ", orderHeaderId="
				+ orderHeaderId + ", updatedBy=" + updatedBy
				+ ", feedbackQuestion=" + feedbackQuestion + ", smiley="
				+ smiley +  ", localTime=" + localTime + "]";
	}

}
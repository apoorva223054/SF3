package com.nirvanaxp.types.entity.snssms;

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
 * The persistent class for the option_type database table.
 * 
 */
@Entity
@Table(name="sms_to_function")
@XmlRootElement(name="sms_to_function")
public class SmsToFunction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name="created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;
	
	@Column(name="updated_by")
	private String updatedBy;
	
	@Column(name="status")
	private String status;
	
	@Column(name="sms_template_id")
	private int smsTemplateId;
	
	@Column(name="function_id")
	private int functionId;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
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

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public int getSmsTemplateId()
	{
		return smsTemplateId;
	}

	public void setSmsTemplateId(int smsTemplateId)
	{
		this.smsTemplateId = smsTemplateId;
	}

	public int getFunctionId()
	{
		return functionId;
	}

	public void setFunctionId(int functionId)
	{
		this.functionId = functionId;
	}

	@Override
	public String toString()
	{
		return "SmsToFunction [id=" + id + ", createdBy=" + createdBy + ", created=" + created + ", updated=" + updated + ", updatedBy=" + updatedBy + ", status=" + status + ", smsTemplateId="
				+ smsTemplateId + ", functionId=" + functionId + "]";
	}

	


	

	

}
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
@Table(name="sms_config")
@XmlRootElement(name="sms_config")
public class SmsConfig implements Serializable {
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
	
	@Column(name="gateway_name")
	private String gatewayName;
	
	@Column(name="gateway_url")
	private String gatewayUrl;
	
	@Column(name="sender_id")
	private String senderId;
	
	

	public SmsConfig() {
	}



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


	public String getStatus()
	{
		return status;
	}



	public void setStatus(String status)
	{
		this.status = status;
	}



	public String getGatewayName()
	{
		return gatewayName;
	}



	public void setGatewayName(String gatewayName)
	{
		this.gatewayName = gatewayName;
	}



	public String getGatewayUrl()
	{
		return gatewayUrl;
	}



	public void setGatewayUrl(String gatewayUrl)
	{
		this.gatewayUrl = gatewayUrl;
	}



	public String getSenderId()
	{
		return senderId;
	}



	public void setSenderId(String senderId)
	{
		this.senderId = senderId;
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
	public String toString()
	{
		return "SmsConfig [id=" + id + ", createdBy=" + createdBy + ", created=" + created + ", updated=" + updated + ", updatedBy=" + updatedBy + ", status=" + status + ", gatewayName="
				+ gatewayName + ", gatewayUrl=" + gatewayUrl + ", senderId=" + senderId + "]";
	}

	

}
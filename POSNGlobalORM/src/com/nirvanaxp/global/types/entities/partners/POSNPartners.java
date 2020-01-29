/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities.partners;


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
@Table(name = "posn_partners")
@XmlRootElement(name = "posn_partners")
public class POSNPartners implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8939613312896238582L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "partner_name")
	private String partnerName;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "account_id")
	private int accountId;

	@Column(name = "business_id")
	private int businessId;

	@Column(name = "status")
	private String status;

	@Column(name = "reference_number")
	private String referenceNumber;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "merchant_id")
	private String merchantId;
	
	@Column(name = "token_id")
	private String tokenId;
	
	@Column(name = "url")
	private String url;

	@Column(name = "email_id")
	private String emailId;
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getPartnerName()
	{
		return partnerName;
	}

	public void setPartnerName(String partnerName)
	{
		this.partnerName = partnerName;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public int getAccountId()
	{
		return accountId;
	}

	public void setAccountId(int accountId)
	{
		this.accountId = accountId;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getReferenceNumber()
	{
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber)
	{
		this.referenceNumber = referenceNumber;
	}

	public int getBusinessId()
	{
		return businessId;
	}

	public void setBusinessId(int businessId)
	{
		this.businessId = businessId;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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
	

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	@Override
	public String toString() {
		return "POSNPartners [id=" + id + ", created=" + created
				+ ", partnerName=" + partnerName + ", displayName="
				+ displayName + ", accountId=" + accountId + ", businessId="
				+ businessId + ", status=" + status + ", referenceNumber="
				+ referenceNumber + ", updated=" + updated + ", updatedBy="
				+ updatedBy + ", createdBy=" + createdBy + ", merchantId="
				+ merchantId + ", tokenId=" + tokenId + ", url=" + url
				+ ", emailId=" + emailId + "]";
	}

	
}

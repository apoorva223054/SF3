/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.types.entities.payment;

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
 * The persistent class for the paymentgateway database table.
 * 
 */
@Entity
@Table(name = "paymentgateway")
@XmlRootElement(name = "paymentgateway")
public class Paymentgateway implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "default_payment_transaction_id")
	private int defaultPaymentTransactionId;

	@Column(name = "locations_id")
	private String locationsId;

	@Column(name = "merchant_id")
	private String merchantId;

	private String password;

	@Column(name = "paymentgateway_type_id")
	private int paymentgatewayTypeId;

	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "license_id")
	private String licenseId;

	@Column(name = "site_id")
	private String siteId;

	@Column(name = "device_id")
	private String deviceId;

	@Column(name = "developer_id")
	private String developerId;

	@Column(name = "version_number")
	private String versionNumber;

	@Column(name = "paymentgateway_transaction_url")
	private String paymentgatewayTransactionUrl;

	public Paymentgateway()
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

	public int getDefaultPaymentTransactionId()
	{
		return this.defaultPaymentTransactionId;
	}

	public void setDefaultPaymentTransactionId(int defaultPaymentTransactionId)
	{
		this.defaultPaymentTransactionId = defaultPaymentTransactionId;
	}

	public String getLocationsId()
	{
		if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	if(locationsId != null && (locationsId.length()==0 || locationsId.equals("0"))){return null;}else{	return locationsId;}}
	}

	public void setLocationsId(String locationsId)
	{
		this.locationsId = locationsId;
	}

	public String getMerchantId()
	{
		return this.merchantId;
	}

	public void setMerchantId(String merchantId)
	{
		this.merchantId = merchantId;
	}

	public String getPassword()
	{
		return this.password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public int getPaymentgatewayTypeId()
	{
		return this.paymentgatewayTypeId;
	}

	public void setPaymentgatewayTypeId(int paymentgatewayTypeId)
	{
		this.paymentgatewayTypeId = paymentgatewayTypeId;
	}

	public String getStatus()
	{
		return this.status;
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

	public String getLicenseId()
	{
		return licenseId;
	}

	public void setLicenseId(String licenseId)
	{
		this.licenseId = licenseId;
	}

	public String getSiteId()
	{
		return siteId;
	}

	public void setSiteId(String siteId)
	{
		this.siteId = siteId;
	}

	public String getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}

	public String getDeveloperId()
	{
		return developerId;
	}

	public void setDeveloperId(String developerId)
	{
		this.developerId = developerId;
	}

	public String getVersionNumber()
	{
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber)
	{
		this.versionNumber = versionNumber;
	}

	public String getPaymentgatewayTransactionUrl()
	{
		return paymentgatewayTransactionUrl;
	}

	public void setPaymentgatewayTransactionUrl(String paymentgatewayTransactionUrl)
	{
		this.paymentgatewayTransactionUrl = paymentgatewayTransactionUrl;
	}

	@Override
	public String toString() {
		return "Paymentgateway [id=" + id + ", created=" + created
				+ ", createdBy=" + createdBy + ", defaultPaymentTransactionId="
				+ defaultPaymentTransactionId + ", locationsId=" + locationsId
				+ ", merchantId=" + merchantId + ", password=" + password
				+ ", paymentgatewayTypeId=" + paymentgatewayTypeId
				+ ", status=" + status + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", licenseId=" + licenseId
				+ ", siteId=" + siteId + ", deviceId=" + deviceId
				+ ", developerId=" + developerId + ", versionNumber="
				+ versionNumber + ", paymentgatewayTransactionUrl="
				+ paymentgatewayTransactionUrl + "]";
	}

}
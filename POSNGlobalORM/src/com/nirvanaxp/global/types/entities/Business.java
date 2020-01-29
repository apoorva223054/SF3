/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The persistent class for the business database table.
 * 
 */
@Entity
@Table(name = "business")
@XmlRootElement(name = "business")
public class Business implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "accounts_id")
	private int accountId;

	@Column(name = "business_auth")
	private byte businessAuth;

	@Column(name = "business_name")
	private String businessName;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	private String email;

	private String logo;

	@Column(name = "sales_tax_rate_1")
	private BigDecimal salesTaxRate1;

	@Column(name = "sales_tax_rate_2")
	private BigDecimal salesTaxRate2;

	@Column(name = "sales_tax_rate_3")
	private BigDecimal salesTaxRate3;

	@Column(name = "schema_name")
	private String schemaName;

	@Column(name = "timezone_id")
	private int timezoneId;

	@Column(name = "transactional_currency_id")
	private int transactionalCurrencyId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "longitude")
	private String longitude;

	@Column(name = "lattitude")
	private String lattitude;
	
	@Column(name = "is_auto_daylight_saving")
	private String isAutoDatlightSaving;

	private String website;

	@ManyToOne
	@JoinColumn(name = "business_type_id")
	private BusinessType businessType;

	@ManyToOne(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "billing_address_id", nullable = false)
	private Address billiAddressId;
	
	@ManyToOne(cascade =
	{ CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "shipping_address_id")
	private Address shippingAddressId;
	
	@Column(name = "max_allowed_devices")
	private int  maxAllowedDevices;	

	@Column(nullable = false, length = 1)
	protected String status;	

	@Column(name = "is_online_app", length = 1)
	private String isOnlineApp;

	public Business()
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

	public byte getBusinessAuth()
	{
		return this.businessAuth;
	}

	public void setBusinessAuth(byte businessAuth)
	{
		this.businessAuth = businessAuth;
	}

	public String getBusinessName()
	{
		return this.businessName;
	}

	public void setBusinessName(String businessName)
	{
		this.businessName = businessName;
	}


	public String getEmail()
	{
		return this.email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getLogo()
	{
		return this.logo;
	}

	public void setLogo(String logo)
	{
		this.logo = logo;
	}

	public BigDecimal getSalesTaxRate1()
	{
		return this.salesTaxRate1;
	}

	public void setSalesTaxRate1(BigDecimal salesTaxRate1)
	{
		this.salesTaxRate1 = salesTaxRate1;
	}

	public BigDecimal getSalesTaxRate2()
	{
		return this.salesTaxRate2;
	}

	public void setSalesTaxRate2(BigDecimal salesTaxRate2)
	{
		this.salesTaxRate2 = salesTaxRate2;
	}

	public BigDecimal getSalesTaxRate3()
	{
		return this.salesTaxRate3;
	}

	public void setSalesTaxRate3(BigDecimal salesTaxRate3)
	{
		this.salesTaxRate3 = salesTaxRate3;
	}

	public String getSchemaName()
	{
		return this.schemaName;
	}

	public void setSchemaName(String schemaName)
	{
		this.schemaName = schemaName;
	}

	public Address getShippingAddressId()
	{
		return this.shippingAddressId;
	}

	public void setShippingAddressId(Address shippingAddressId)
	{
		this.shippingAddressId = shippingAddressId;
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

	public String getWebsite()
	{
		return this.website;
	}

	public void setWebsite(String website)
	{
		this.website = website;
	}

	public int getAccountId()
	{
		return accountId;
	}

	public void setAccountId(int accountId)
	{
		this.accountId = accountId;
	}

	public int getTimezoneId()
	{
		return timezoneId;
	}

	public void setTimezoneId(int timezoneId)
	{
		this.timezoneId = timezoneId;
	}

	public int getTransactionalCurrencyId()
	{
		return transactionalCurrencyId;
	}

	public void setTransactionalCurrencyId(int transactionalCurrencyId)
	{
		this.transactionalCurrencyId = transactionalCurrencyId;
	}

	public BusinessType getBusinessType()
	{
		return businessType;
	}

	public void setBusinessType(BusinessType businessType)
	{
		this.businessType = businessType;
	}

	public Address getBilliAddressId()
	{
		return billiAddressId;
	}

	public void setBilliAddressId(Address billiAddressId)
	{
		this.billiAddressId = billiAddressId;
	}

	public String getLongitude()
	{
		return longitude;
	}

	public void setLongitude(String longitude)
	{
		this.longitude = longitude;
	}

	public String getLattitude()
	{
		return lattitude;
	}

	public void setLattitude(String lattitude)
	{
		this.lattitude = lattitude;
	}

	public int getMaxAllowedDevices()
	{
		return maxAllowedDevices;
	}

	public void setMaxAllowedDevices(int maxAllowedDevices)
	{
		this.maxAllowedDevices = maxAllowedDevices;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIsOnlineApp() {
		return isOnlineApp;
	}

	public void setIsOnlineApp(String   isOnlineApp) {
		this.isOnlineApp = isOnlineApp;
	}

	public String getIsAutoDatlightSaving() {
		return isAutoDatlightSaving;
	}

	public void setIsAutoDatlightSaving(String isAutoDatlightSaving) {
		this.isAutoDatlightSaving = isAutoDatlightSaving;
	}

	@Override
	public String toString() {
		return "Business [id=" + id + ", accountId=" + accountId + ", businessAuth=" + businessAuth + ", businessName="
				+ businessName + ", created=" + created + ", createdBy=" + createdBy + ", email=" + email + ", logo="
				+ logo + ", salesTaxRate1=" + salesTaxRate1 + ", salesTaxRate2=" + salesTaxRate2 + ", salesTaxRate3="
				+ salesTaxRate3 + ", schemaName=" + schemaName + ", timezoneId=" + timezoneId
				+ ", transactionalCurrencyId=" + transactionalCurrencyId + ", updated=" + updated + ", updatedBy="
				+ updatedBy + ", longitude=" + longitude + ", lattitude=" + lattitude + ", isAutoDatlightSaving="
				+ isAutoDatlightSaving + ", website=" + website + ", businessType=" + businessType + ", billiAddressId="
				+ billiAddressId + ", shippingAddressId=" + shippingAddressId + ", maxAllowedDevices="
				+ maxAllowedDevices + ", status=" + status + ", isOnlineApp=" + isOnlineApp + "]";
	}

	
	
	
}
/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities.accounts;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.nirvanaxp.global.types.entities.Address;

/**
 * The persistent class for the accounts database table.
 * 
 */
@Entity
@Table(name = "accounts")
@XmlRootElement(name = "accounts")
public class Account implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(unique = true, nullable = false)
	private int id;

	@Column(name = "billing_address_id")
	private String billingAddressId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "email")
	private String email;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "logo")
	private String logo;

	@Column(name = "name")
	private String name;

	@Column(name = "shipping_address_id")
	private String shippingAddressId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "website")
	private String website;

	@Column(name = "schema_name")
	private String schemaName;

	@Column(name = "max_allowed_devices")
	private int maxAllowedDevices;

	@Column(name = "is_local_account")
	private int isLocalAccount;

	@Column(name = "local_server_url")
	private String localServerUrl;

	private transient Address shippingAddress;

	private transient Address billingAddress;

	@Column(name = "is_sms_account")
	private int isSmsAccount;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "account_to_server_config", joinColumns =
	{ @JoinColumn(name = "accounts_id", nullable = false) }, inverseJoinColumns =
	{ @JoinColumn(name = "server_config_id", nullable = false) })
	private Set<ServerConfig> serverConfigs;

	public Account()
	{
	}

	public Account(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getEmail()
	{
		return this.email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getFirstName()
	{
		return this.firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return this.lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getLogo()
	{
		return this.logo;
	}

	public void setLogo(String logo)
	{
		this.logo = logo;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
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

	 

	public String getBillingAddressId()
	{
		return billingAddressId;
	}

	public void setBillingAddressId(String billingAddressId)
	{
		this.billingAddressId = billingAddressId;
	}

	public void setShippingAddressId(String shippingAddressId)
	{
		this.shippingAddressId = shippingAddressId;
	}

	public String getSchemaName()
	{
		return schemaName;
	}

	public void setSchemaName(String schemaName)
	{
		this.schemaName = schemaName;
	}

	public Address getShippingAddress()
	{
		return shippingAddress;
	}

	public void setShippingAddress(Address shippingAddress)
	{
		this.shippingAddress = shippingAddress;
	}

	public Address getBillingAddress()
	{
		return billingAddress;
	}

	public void setBillingAddress(Address billingAddress)
	{
		this.billingAddress = billingAddress;
	}

	public int getMaxAllowedDevices()
	{
		return maxAllowedDevices;
	}

	public void setMaxAllowedDevices(int maxAllowedDevices)
	{
		this.maxAllowedDevices = maxAllowedDevices;
	}

	public boolean equals(Account account)
	{
		if (account instanceof Account && ((Account) account).getId() == this.id)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public int getIsLocalAccount()
	{
		return isLocalAccount;
	}

	public String getLocalServerUrl()
	{
		return localServerUrl;
	}

	public void setIsLocalAccount(int isLocalAccount)
	{
		this.isLocalAccount = isLocalAccount;
	}

	public void setLocalServerUrl(String localServerUrl)
	{
		this.localServerUrl = localServerUrl;
	}

	public int getIsSmsAccount()
	{
		return isSmsAccount;
	}

	public void setIsSmsAccount(int isSmsAccount)
	{
		this.isSmsAccount = isSmsAccount;
	}

	public Set<ServerConfig> getServerConfigs()
	{
		return serverConfigs;
	}

	public void setServerConfigs(Set<ServerConfig> serverConfigs)
	{
		this.serverConfigs = serverConfigs;
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
		return "Account [id=" + id + ", billingAddressId=" + billingAddressId
				+ ", created=" + created + ", createdBy=" + createdBy
				+ ", email=" + email + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", logo=" + logo + ", name="
				+ name + ", shippingAddressId=" + shippingAddressId
				+ ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", website=" + website + ", schemaName=" + schemaName
				+ ", maxAllowedDevices=" + maxAllowedDevices
				+ ", isLocalAccount=" + isLocalAccount + ", localServerUrl="
				+ localServerUrl + ", isSmsAccount=" + isSmsAccount
				+ ", serverConfigs=" + serverConfigs + "]";
	}

}
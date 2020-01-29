/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities;

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

import com.nirvanaxp.global.types.entities.accounts.Account;

/**
 * The persistent class for the users database table.
 * 
 */
@Entity
@Table(name = "users")
@XmlRootElement(name = "users")
public class User implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "last_login_ts")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLoginTs;

	private String password;

	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	private String username;

	@Column(name = "auth_pin", length = 64)
	private String authPin;

	@Column(name = "first_name", length = 40)
	private String firstName;

	@Column(name = "last_name", length = 40)
	private String lastName;

	@Column(name = "date_of_birth", length = 10)
	private String dateofbirth;

	@Column(length = 64)
	private String email;

	@Column(name = "phone")
	private String phone;
	
	@Column(name = "qrCodePath")
	private String qrCodePath;

	
	@Column(name = "country_id")
	private int countryId;

	private transient String error;

	// uni-directional many-to-many association to Discount
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "users_to_address", joinColumns =
	{ @JoinColumn(name = "users_id", nullable = false) }, inverseJoinColumns =
	{ @JoinColumn(name = "address_id", nullable = false) })
	private Set<Address> addressesSet;

	// uni-directional many-to-many association to Discount
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_to_roles", joinColumns =
	{ @JoinColumn(name = "users_id", nullable = false) }, inverseJoinColumns =
	{ @JoinColumn(name = "roles_id", nullable = false) })
	private Set<Role> rolesSet;

	// uni-directional many-to-many association to Discount
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_to_business", joinColumns =
	{ @JoinColumn(name = "users_id", nullable = false) }, inverseJoinColumns =
	{ @JoinColumn(name = "business_id", nullable = false) })
	private Set<Business> businessesSet;

	// uni-directional many-to-many association to Discount
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_to_accounts", joinColumns =
	{ @JoinColumn(name = "users_id", nullable = false) }, inverseJoinColumns =
	{ @JoinColumn(name = "accounts_id", nullable = false) })
	private Set<Account> accountsSet;

	public User()
	{
	}

	public User(String createdBy, String password, String status, String updatedBy, String username, String authPin, String firstName, String lastName, String phoneNumber, String email, String dateOfBirth,int countryId)
	{
		super();
		this.createdBy = createdBy;
		this.password = password;
		this.status = status;
		this.updatedBy = updatedBy;
		this.username = username;
		this.authPin = authPin;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phoneNumber;
		this.email = email;
		this.dateofbirth = dateOfBirth;
		this.countryId =countryId;
	}

	public void updateUser(String createdBy, String password, String status, String updatedBy, String username, String authPin, String firstName, String lastName, String phoneNumber, String email,
			String dateOfBirth)
	{
		this.createdBy = createdBy;
		this.password = password;
		this.status = status;
		this.updatedBy = updatedBy;
		this.username = username;
		this.authPin = authPin;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phoneNumber;
		this.email = email;
		this.dateofbirth = dateOfBirth;
	}


	public String getPassword()
	{
		return this.password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getStatus()
	{
		return this.status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getUsername()
	{
		return this.username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getAuthPin()
	{
		return authPin;
	}

	public void setAuthPin(String authPin)
	{
		this.authPin = authPin;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getDateofbirth()
	{
		return dateofbirth;
	}

	public void setDateofbirth(String dateofbirth)
	{
		this.dateofbirth = dateofbirth;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public void setCreated(Date created)
	{
		this.created = created;
	}

	public void setLastLoginTs(Date lastLoginTs)
	{
		this.lastLoginTs = lastLoginTs;
	}

	public void setUpdated(Date updated)
	{
		this.updated = updated;
	}

	public Set<Role> getRolesSet()
	{
		return rolesSet;
	}

	public void setRolesSet(Set<Role> rolesSet)
	{
		this.rolesSet = rolesSet;
	}

	public Set<Business> getBusinessesSet()
	{
		return businessesSet;
	}

	public void setBusinessesSet(Set<Business> businessesSet)
	{
		this.businessesSet = businessesSet;
	}

	public Set<Address> getAddressesSet()
	{
		return addressesSet;
	}

	public void setAddressesSet(Set<Address> addressesSet)
	{
		this.addressesSet = addressesSet;
	}

	public String getError()
	{
		return error;
	}

	public void setError(String error)
	{
		this.error = error;
	}

	public Set<Account> getAccountsSet()
	{
		return accountsSet;
	}

	public void setAccountsSet(Set<Account> accountsSet)
	{
		this.accountsSet = accountsSet;
	}

	public Date getCreated()
	{
		return created;
	}

	public Date getLastLoginTs()
	{
		return lastLoginTs;
	}

	public Date getUpdated()
	{
		return updated;
	}

	public String getQrCodePath() {
		return qrCodePath;
	}

	public void setQrCodePath(String qrCodePath) {
		this.qrCodePath = qrCodePath;
	}
	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		return "User [id=" + id + ", created=" + created + ", createdBy="
				+ createdBy + ", lastLoginTs=" + lastLoginTs + ", password="
				+ password + ", status=" + status + ", updated=" + updated
				+ ", updatedBy=" + updatedBy + ", username=" + username
				+ ", authPin=" + authPin + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", dateofbirth=" + dateofbirth
				+ ", email=" + email + ", phone=" + phone + ", qrCodePath="
				+ qrCodePath + ", countryId=" + countryId + ", addressesSet="
				+ addressesSet + ", rolesSet=" + rolesSet + ", businessesSet="
				+ businessesSet + ", accountsSet=" + accountsSet + "]";
	}

	

	

}
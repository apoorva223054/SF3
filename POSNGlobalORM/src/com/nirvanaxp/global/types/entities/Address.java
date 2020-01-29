/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.global.types.entities;

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
 * The persistent class for the address database table.
 * 
 */
@Entity
@Table(name = "address")
@XmlRootElement(name = "address")
public class Address implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private String id;

	@Column(name = "address1")
	private String address1;

	@Column(name = "address2")
	private String address2;

	@Column(name = "city")
	private String city;

	@Column(name = "country_id")
	private int countryId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created")
	private Date created;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "fax")
	private String fax;

	@Column(name = "lat_value")
	private String latValue;

	@Column(name = "long_value")
	private String longValue;

	@Column(name = "phone")
	private String phone;

	@Column(name = "state")
	private String state;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated")
	private Date updated;

	@Column(name = "updated_by")
	private String updatedBy;

	@Column(name = "zip")
	private String zip;

	@Column(name = "state_id", nullable = false)
	private Integer stateId;
	
	@Column(name = "city_id", nullable = false)
	private Integer cityId;
	
	@Column(name = "is_default_address")
	private int isDefaultAddress;

	public Address(String address1, String address2, String city, int countryId, String createdBy, String fax, String latValue, String longValue, String phone, String state, String updatedBy, String zip,
			String id, Integer stateId, Integer cityId,int isDefaultAddress)
	{
		super();
		this.address1 = address1;
		this.address2 = address2;
		this.city = city;
		this.countryId = countryId;
		this.createdBy = createdBy;
		this.fax = fax;
		this.latValue = latValue;
		this.longValue = longValue;
		this.phone = phone;
		this.state = state;
		this.updatedBy = updatedBy;
		this.zip = zip;
		this.id = id;
		this.stateId = stateId;
		this.cityId = cityId;
		this.isDefaultAddress = isDefaultAddress;
	}

	public Address()
	{
	}

	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAddress1()
	{
		return this.address1;
	}

	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}

	public String getAddress2()
	{
		return this.address2;
	}

	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}

	public String getCity()
	{
		return this.city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getFax()
	{
		return this.fax;
	}

	public void setFax(String fax)
	{
		this.fax = fax;
	}

	public String getLatValue()
	{
		return this.latValue;
	}

	public void setLatValue(String latValue)
	{
		this.latValue = latValue;
	}

	public String getLongValue()
	{
		return this.longValue;
	}

	public void setLongValue(String longValue)
	{
		this.longValue = longValue;
	}

	public String getPhone()
	{
		return this.phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getState()
	{
		return this.state;
	}

	public void setState(String state)
	{
		this.state = state;
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

	public String getZip()
	{
		return this.zip;
	}

	public void setZip(String zip)
	{
		this.zip = zip;
	}

	@Override
	public boolean equals(Object object)
	{

		if (object != null && object instanceof Address)
		{
			Address otherAddress = (Address) object;
			if (address1 != null && address1.trim().length() > 0 && otherAddress.getAddress1() != null && otherAddress.getAddress1().length() > 0)
			{
				if (address1.trim().equalsIgnoreCase(otherAddress.getAddress1().trim()))
				{
					return true;
				}
			}

		}

		return false;
	}

	public int getCountryId()
	{
		return countryId;
	}

	public void setCountryId(int countryId)
	{
		this.countryId = countryId;
	}

	/**
	 * @return the isDefaultAddress
	 */
	public int getIsDefaultAddress()
	{
		return isDefaultAddress;
	}

	/**
	 * @param isDefaultAddress
	 *            the isDefaultAddress to set
	 */
	public void setIsDefaultAddress(int isDefaultAddress)
	{
		this.isDefaultAddress = isDefaultAddress;
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

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	@Override
	public String toString() {
		return "Address [id=" + id + ", address1=" + address1 + ", address2="
				+ address2 + ", city=" + city + ", countryId=" + countryId
				+ ", created=" + created + ", createdBy=" + createdBy
				+ ", fax=" + fax + ", latValue=" + latValue + ", longValue="
				+ longValue + ", phone=" + phone + ", state=" + state
				+ ", updated=" + updated + ", updatedBy=" + updatedBy
				+ ", zip=" + zip + ", stateId=" + stateId + ", cityId="
				+ cityId + ", isDefaultAddress=" + isDefaultAddress + "]";
	}


}
